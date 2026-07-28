package com.example.muscleiq.data.model

import java.util.Date
import java.util.UUID

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val createdAt: Date = Date()
)

data class Workout(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val name: String = "",
    val durationMinutes: Int = 0,
    val date: Date = Date(),
    val exercises: List<Exercise> = emptyList()
)

data class Exercise(
    val exerciseName: String = "",
    val primaryMuscle: String = "",
    val sets: List<ExerciseSet> = emptyList()
)

data class ExerciseSet(
    val reps: Int = 0,
    val weight: Double = 0.0
)
