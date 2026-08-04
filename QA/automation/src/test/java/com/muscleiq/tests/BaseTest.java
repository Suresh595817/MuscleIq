package com.muscleiq.tests;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public class BaseTest {

    protected AndroidDriver driver;

    @BeforeMethod
    public void setUp() {
        UiAutomator2Options options = new UiAutomator2Options();
        options.setPlatformName("Android");
        // Update device name to match GitHub Actions emulator or your local emulator
        options.setDeviceName("emulator-5554"); 
        options.setAppPackage("com.example.muscleiq");
        options.setAppActivity(".MainActivity");
        options.setAutomationName("UiAutomator2");
        options.setNoReset(false); // Clean install state for tests

        try {
            driver = new AndroidDriver(new URL("http://127.0.0.1:4723"), options);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            System.out.println("Appium Driver Started Successfully.");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException("Appium Server URL is invalid");
        }
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("Appium Driver Quit Successfully.");
        }
    }
}
