package com.example.muscleiq.data.repository

import com.example.muscleiq.data.model.Workout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Date

class WorkoutRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun saveWorkout(workout: Workout): Result<Unit> {
        return try {
            firestore.collection("Workouts").document(workout.id).set(workout).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWorkoutsForUser(userId: String): Result<List<Workout>> {
        return try {
            val snapshot = firestore.collection("Workouts")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            val workouts = snapshot.toObjects(Workout::class.java).sortedByDescending { it.date }
            Result.success(workouts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRecentWorkouts(userId: String, days: Int = 30): Result<List<Workout>> {
        return try {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -days)
            val thirtyDaysAgo = calendar.time

            val snapshot = firestore.collection("Workouts")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            val workouts = snapshot.toObjects(Workout::class.java).filter { it.date >= thirtyDaysAgo }
            Result.success(workouts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
