package com.example.muscleiq.data.repository

import com.example.muscleiq.data.model.Workout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.Calendar
import java.util.Date
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.google.gson.GsonBuilder

class WorkoutRepository {
    // 127.0.0.1 mapped via adb reverse tcp:5000 tcp:5000
    private val baseUrl = "http://127.0.0.1:5000/api/workouts"
    private val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create()
    suspend fun saveWorkout(workout: Workout): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val url = URL(baseUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val json = gson.toJson(workout)
            val writer = OutputStreamWriter(connection.outputStream)
            writer.write(json)
            writer.flush()
            writer.close()

            if (connection.responseCode in 200..299) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("HTTP Error ${connection.responseCode}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWorkoutsForUser(userId: String): Result<List<Workout>> = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl?userId=$userId")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            if (connection.responseCode in 200..299) {
                val reader = InputStreamReader(connection.inputStream)
                val type = object : TypeToken<List<Workout>>() {}.type
                val workouts: List<Workout> = gson.fromJson(reader, type)
                reader.close()
                Result.success(workouts.sortedByDescending { it.date })
            } else {
                Result.failure(Exception("HTTP Error ${connection.responseCode}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRecentWorkouts(userId: String, days: Int = 30): Result<List<Workout>> {
        return getWorkoutsForUser(userId)
    }
}
