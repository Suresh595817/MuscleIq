package com.example.muscleiq.data

data class StrengthRecord(
    val date: String,
    val maxWeight: Float
)

data class VolumeRecord(
    val dayOfWeek: String,
    val totalVolume: Float
)
