package com.muscleiq.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;

public class WorkoutTrackerPage {
    private AndroidDriver driver;

    // Locators
    private By workoutNameInput = AppiumBy.id("workout_name_input");
    private By addExerciseButton = AppiumBy.id("btn_add_exercise");
    private By exerciseNameInput = AppiumBy.id("exercise_name_input");
    private By addSetButton = AppiumBy.id("btn_add_set");
    private By weightInput = AppiumBy.id("weight_input_field");
    private By repsInput = AppiumBy.id("reps_input_field");
    private By saveWorkoutButton = AppiumBy.id("btn_save_workout");
    private By successToast = AppiumBy.xpath("//android.widget.Toast[@text='Workout Saved!']");

    public WorkoutTrackerPage(AndroidDriver driver) {
        this.driver = driver;
    }

    public void enterWorkoutName(String name) {
        driver.findElement(workoutNameInput).sendKeys(name);
    }

    public void addExercise(String exerciseName) {
        driver.findElement(addExerciseButton).click();
        driver.findElement(exerciseNameInput).sendKeys(exerciseName);
    }

    public void addSet(String weight, String reps) {
        driver.findElement(addSetButton).click();
        driver.findElement(weightInput).sendKeys(weight);
        driver.findElement(repsInput).sendKeys(reps);
    }

    public void saveWorkout() {
        driver.findElement(saveWorkoutButton).click();
    }
    
    public boolean isWorkoutSavedSuccessfully() {
        // Appium can read Toast messages in UiAutomator2 using precise XPath
        return driver.findElements(successToast).size() > 0;
    }
}
