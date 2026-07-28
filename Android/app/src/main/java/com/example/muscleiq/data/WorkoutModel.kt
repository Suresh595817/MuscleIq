package com.example.muscleiq.data

import com.google.gson.annotations.SerializedName

data class GeneratedWorkout(
    @SerializedName("title")
    val title: String,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("exercises")
    val exercises: List<GeneratedExercise>
)

data class GeneratedExercise(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("sets")
    val sets: String,
    
    @SerializedName("reps")
    val reps: String,
    
    @SerializedName("rest_time")
    val restTime: String,
    
    @SerializedName("tip")
    val tip: String
)
