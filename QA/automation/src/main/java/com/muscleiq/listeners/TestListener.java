package com.muscleiq.listeners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        System.out.println("Starting Enterprise Execution: Initializing Extent HTML Reports and Apache POI Excel Workbooks...");
        // ExtentReportManager.initReports();
        // ExcelReportManager.initExcel();
    }

    @Override
    public void onTestStart(ITestResult result) {
        Object[] parameters = result.getParameters();
        String testId = parameters.length > 0 ? parameters[0].toString() : result.getName();
        // ExtentReportManager.createTest(testId);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        Object[] parameters = result.getParameters();
        String testId = parameters.length > 0 ? parameters[0].toString() : result.getName();
        System.out.println("PASSED: " + testId);
        // ExtentReportManager.getTest().pass("Test Passed");
        // ExcelReportManager.updateStatus(testId, "Pass");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        Object[] parameters = result.getParameters();
        String testId = parameters.length > 0 ? parameters[0].toString() : result.getName();
        System.out.println("FAILED: " + testId + " - " + result.getThrowable().getMessage());
        // String screenshotPath = ScreenshotUtils.captureScreenshot(testId);
        // ExtentReportManager.getTest().fail(result.getThrowable()).addScreenCaptureFromPath(screenshotPath);
        // ExcelReportManager.updateStatus(testId, "Fail");
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("Execution Complete. Flushing Reports to /reports directory.");
        // ExtentReportManager.flushReports();
        // ExcelReportManager.writeExcel();
    }
}
