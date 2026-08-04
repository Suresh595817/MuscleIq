package com.muscleiq.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;

public class LoginPage {
    private AndroidDriver driver;

    // Locators
    private By emailInput = AppiumBy.id("email_input_field");
    private By passwordInput = AppiumBy.id("password_input_field");
    private By loginButton = AppiumBy.id("login_button");
    private By registerButton = AppiumBy.id("register_link_button");
    private By errorMessage = AppiumBy.id("error_text_view");

    public LoginPage(AndroidDriver driver) {
        this.driver = driver;
    }

    public void enterEmail(String email) {
        driver.findElement(emailInput).sendKeys(email);
    }

    public void enterPassword(String password) {
        driver.findElement(passwordInput).sendKeys(password);
    }

    public void clickLogin() {
        driver.findElement(loginButton).click();
    }

    public void clickRegister() {
        driver.findElement(registerButton).click();
    }
    
    public String getErrorMessage() {
        return driver.findElement(errorMessage).getText();
    }

    public void loginAs(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickLogin();
    }
}
