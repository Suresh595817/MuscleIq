package com.example.muscleiq.data.model

object WorkoutLibrary {
    val exercises = listOf(
        // Chest
        ExerciseDef("Barbell Bench Press", "Chest"),
        ExerciseDef("Incline Dumbbell Press", "Chest"),
        ExerciseDef("Push-Ups", "Chest"),
        ExerciseDef("Cable Crossovers", "Chest"),
        ExerciseDef("Chest Dip", "Chest"),

        // Back
        ExerciseDef("Pull-Ups", "Back"),
        ExerciseDef("Lat Pulldown", "Back"),
        ExerciseDef("Barbell Row", "Back"),
        ExerciseDef("Seated Cable Row", "Back"),
        ExerciseDef("Deadlift", "Back"),

        // Shoulders
        ExerciseDef("Overhead Press", "Shoulders"),
        ExerciseDef("Lateral Raises", "Shoulders"),
        ExerciseDef("Arnold Press", "Shoulders"),
        ExerciseDef("Face Pulls", "Shoulders"),

        // Legs
        ExerciseDef("Barbell Squat", "Quads"),
        ExerciseDef("Leg Press", "Quads"),
        ExerciseDef("Bulgarian Split Squat", "Quads"),
        ExerciseDef("Romanian Deadlift", "Hamstrings"),
        ExerciseDef("Leg Curl", "Hamstrings"),
        ExerciseDef("Calf Raises", "Calves"),

        // Arms
        ExerciseDef("Barbell Curl", "Biceps"),
        ExerciseDef("Hammer Curl", "Biceps"),
        ExerciseDef("Tricep Pushdown", "Triceps"),
        ExerciseDef("Overhead Tricep Extension", "Triceps"),
        
        // Core
        ExerciseDef("Crunch", "Core"),
        ExerciseDef("Plank", "Core"),
        ExerciseDef("Leg Raises", "Core")
    )
}

data class ExerciseDef(
    val name: String,
    val primaryMuscle: String
)
