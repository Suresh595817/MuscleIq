package com.muscleiq.tests;

import com.muscleiq.pages.DashboardPage;
import com.muscleiq.pages.LoginPage;
import com.muscleiq.pages.WorkoutTrackerPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CoreE2ETests extends BaseTest {

    @Test(priority = 1)
    public void testSuccessfulLogin() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = new DashboardPage(driver);

        System.out.println("Executing Login Test...");
        loginPage.loginAs("testuser@muscleiq.com", "Password123!");
        
        Assert.assertTrue(dashboardPage.isDashboardLoaded(), "Dashboard failed to load after login.");
    }

    @Test(priority = 2)
    public void testLogNewWorkout() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = new DashboardPage(driver);
        WorkoutTrackerPage trackerPage = new WorkoutTrackerPage(driver);

        System.out.println("Executing Workout Logging E2E Flow...");
        loginPage.loginAs("testuser@muscleiq.com", "Password123!");
        
        dashboardPage.clickStartWorkout();
        
        trackerPage.enterWorkoutName("Heavy Chest Day");
        trackerPage.addExercise("Bench Press");
        trackerPage.addSet("225", "8");
        trackerPage.addSet("225", "6");
        
        trackerPage.saveWorkout();
        
        Assert.assertTrue(trackerPage.isWorkoutSavedSuccessfully(), "Workout Save Toast Message Not Found!");
    }
}
