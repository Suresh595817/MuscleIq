package com.muscleiq.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;

public class DashboardPage {
    private AndroidDriver driver;

    // Locators
    private By welcomeMessage = AppiumBy.id("welcome_text");
    private By startWorkoutButton = AppiumBy.id("fab_start_workout");
    private By generateAiWorkoutButton = AppiumBy.id("nav_ai_generator");
    private By profileIcon = AppiumBy.id("nav_profile");

    public DashboardPage(AndroidDriver driver) {
        this.driver = driver;
    }

    public boolean isDashboardLoaded() {
        return driver.findElements(welcomeMessage).size() > 0;
    }

    public void clickStartWorkout() {
        driver.findElement(startWorkoutButton).click();
    }

    public void navigateToAiGenerator() {
        driver.findElement(generateAiWorkoutButton).click();
    }
    
    public void navigateToProfile() {
        driver.findElement(profileIcon).click();
    }
}
