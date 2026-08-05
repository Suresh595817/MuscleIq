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
            Sheet sheet = workbook.createSheet("Test Results");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Test ID");
            header.createCell(1).setCellValue("Module");
            header.createCell(2).setCellValue("Scenario");
            header.createCell(3).setCellValue("Status");

            int rowNum = 1;
            for (TestResultData res : resultsList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(res.testId);
                row.createCell(1).setCellValue(res.module);
                row.createCell(2).setCellValue(res.name);
                row.createCell(3).setCellValue(res.status);
            }

            try (FileOutputStream fileOut = new FileOutputStream("reports/appium-report.xlsx")) {
                workbook.write(fileOut);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
