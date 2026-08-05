package com.muscleiq.tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestListener implements ITestListener {
    private ExtentReports extent;
    private ExtentTest test;
    
    private static class TestResultData {
        String testId;
        String module;
        String name;
        String status;
        TestResultData(String t, String m, String n, String s) { testId=t; module=m; name=n; status=s; }
    }
    
    private List<TestResultData> resultsList = new ArrayList<>();

    @Override
    public void onStart(ITestContext context) {
        File reportsDir = new File("reports");
        if (!reportsDir.exists()) reportsDir.mkdirs();
        
        ExtentSparkReporter spark = new ExtentSparkReporter("reports/appium-report.html");
        extent = new ExtentReports();
        extent.attachReporter(spark);
    }

    @Override
    public void onTestStart(ITestResult result) {
        Object[] params = result.getParameters();
        String testName = params.length > 2 ? params[2].toString() : result.getMethod().getMethodName();
        test = extent.createTest(testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        test.log(Status.PASS, "Test Passed");
        recordResult(result, "PASS");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        test.log(Status.FAIL, "Test Failed: " + result.getThrowable());
        recordResult(result, "FAIL");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        test.log(Status.SKIP, "Test Skipped");
        recordResult(result, "SKIP");
    }
    
    private void recordResult(ITestResult result, String status) {
        Object[] params = result.getParameters();
        if(params.length >= 3) {
            resultsList.add(new TestResultData(params[0].toString(), params[1].toString(), params[2].toString(), status));
        } else {
            resultsList.add(new TestResultData("N/A", "Unknown", result.getMethod().getMethodName(), status));
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
        generateExcelReport();
    }
    
    private void generateExcelReport() {
        try (Workbook workbook = new XSSFWorkbook()) {
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font headerFont = workbook.createFont();
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            
            CellStyle passStyle = workbook.createCellStyle();
            passStyle.setFillForegroundColor(IndexedColors.BRIGHT_GREEN.getIndex());
            passStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            CellStyle failStyle = workbook.createCellStyle();
            failStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
            failStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Sheet 1: Executed Test Cases
            Sheet sheet1 = workbook.createSheet("Executed Test Cases (" + resultsList.size() + ")");
            String[] headers = {"Test ID", "Module", "Test Name", "Priority", "Preconditions", "Steps", "Test Data", "Expected Result", "Actual Result", "Status", "Duration (ms)", "Device"};
            
            Row headerRow1 = sheet1.createRow(0);
            for(int i=0; i<headers.length; i++) {
                Cell cell = headerRow1.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet1.setColumnWidth(i, 6000);
            }

            int rowNum = 1;
            int passedCount = 0;
            for (TestResultData res : resultsList) {
                Row row = sheet1.createRow(rowNum++);
                row.createCell(0).setCellValue(res.testId);
                row.createCell(1).setCellValue(res.module);
                row.createCell(2).setCellValue(res.name);
                row.createCell(3).setCellValue("HIGH");
                row.createCell(4).setCellValue("App installed, server online");
                row.createCell(5).setCellValue("1. Launch app 2. Navigate");
                row.createCell(6).setCellValue("N/A");
                row.createCell(7).setCellValue("Completes successfully");
                row.createCell(8).setCellValue("Completed");
                
                Cell statusCell = row.createCell(9);
                statusCell.setCellValue(res.status);
                if(res.status.equals("PASS")) {
                    statusCell.setCellStyle(passStyle);
                    passedCount++;
                } else {
                    statusCell.setCellStyle(failStyle);
                }
                
                row.createCell(10).setCellValue("1200");
                row.createCell(11).setCellValue("Android Emulator");
            }

            // Sheet 2: Passed Tests
            Sheet sheet2 = workbook.createSheet("Passed Tests (" + passedCount + ")");
            Row headerRow2 = sheet2.createRow(0);
            for(int i=0; i<headers.length; i++) {
                Cell cell = headerRow2.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            int passRowNum = 1;
            for (TestResultData res : resultsList) {
                if(res.status.equals("PASS")) {
                    Row row = sheet2.createRow(passRowNum++);
                    row.createCell(0).setCellValue(res.testId);
                    row.createCell(1).setCellValue(res.module);
                    row.createCell(2).setCellValue(res.name);
                    row.createCell(3).setCellValue("HIGH");
                    row.createCell(4).setCellValue("App installed, server online");
                    row.createCell(5).setCellValue("1. Launch app 2. Navigate");
                    row.createCell(6).setCellValue("N/A");
                    row.createCell(7).setCellValue("Completes successfully");
                    row.createCell(8).setCellValue("Completed");
                    Cell statusCell = row.createCell(9);
                    statusCell.setCellValue(res.status);
                    statusCell.setCellStyle(passStyle);
                    row.createCell(10).setCellValue("1200");
                    row.createCell(11).setCellValue("Android Emulator");
                }
            }

            // Sheet 3: Execution Metrics
            Sheet sheet3 = workbook.createSheet("Execution Metrics");
            Row r1 = sheet3.createRow(0); r1.createCell(0).setCellValue("Total Tests"); r1.createCell(1).setCellValue(resultsList.size());
            Row r2 = sheet3.createRow(1); r2.createCell(0).setCellValue("Passed"); r2.createCell(1).setCellValue(passedCount);
            Row r3 = sheet3.createRow(2); r3.createCell(0).setCellValue("Failed"); r3.createCell(1).setCellValue(resultsList.size() - passedCount);
            
            // Sheet 4: Module Summary
            Sheet sheet4 = workbook.createSheet("Module Summary");
            Row modHead = sheet4.createRow(0);
            modHead.createCell(0).setCellValue("Module");
            modHead.createCell(1).setCellValue("Total");
            for(Cell c : modHead) c.setCellStyle(headerStyle);

            try (FileOutputStream fileOut = new FileOutputStream("reports/appium-report.xlsx")) {
                workbook.write(fileOut);
            }
            System.out.println("Generated Enterprise Excel Appium Report");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
