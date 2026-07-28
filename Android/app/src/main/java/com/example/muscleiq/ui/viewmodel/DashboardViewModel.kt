package com.example.muscleiq.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.muscleiq.data.model.Workout
import com.example.muscleiq.data.repository.AuthRepository
import com.example.muscleiq.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.example.muscleiq.data.model.User

sealed class DashboardState {
    object Loading : DashboardState()
    data class Success(
        val user: User?,
        val recentWorkouts: List<Workout>,
        val imbalanceScore: Int,
        val imbalanceWarning: String
    ) : DashboardState()
    data class Error(val message: String) : DashboardState()
}

class DashboardViewModel(
    private val workoutRepository: WorkoutRepository = WorkoutRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _dashboardState = MutableStateFlow<DashboardState>(DashboardState.Loading)
    val dashboardState: StateFlow<DashboardState> = _dashboardState.asStateFlow()

    fun loadDashboardData() {
        _dashboardState.value = DashboardState.Loading
        val userId = authRepository.getCurrentUserId()
        if (userId == null) {
            _dashboardState.value = DashboardState.Error("User not logged in")
            return
        }

        viewModelScope.launch {
            val userResult = authRepository.getCurrentUserData()
            val user = userResult.getOrNull()
            
            val result = workoutRepository.getRecentWorkouts(userId, 30)
            result.onSuccess { workouts ->
                val (score, warning) = calculateImbalanceScore(workouts)
                _dashboardState.value = DashboardState.Success(user, workouts, score, warning)
            }.onFailure { error ->
                _dashboardState.value = DashboardState.Error(error.localizedMessage ?: "Failed to load workouts")
            }
        }
    }

    private fun calculateImbalanceScore(workouts: List<Workout>): Pair<Int, String> {
        if (workouts.isEmpty()) return Pair(100, "No workouts logged yet. Start training!")

        val muscleSets = mutableMapOf<String, Int>().withDefault { 0 }
        
        for (workout in workouts) {
            for (exercise in workout.exercises) {
                val currentSets = muscleSets.getValue(exercise.primaryMuscle)
                muscleSets[exercise.primaryMuscle] = currentSets + exercise.sets.size
            }
        }

        val pushSets = (muscleSets["Chest"] ?: 0) + (muscleSets["Shoulders"] ?: 0) + (muscleSets["Triceps"] ?: 0)
        val pullSets = (muscleSets["Back"] ?: 0) + (muscleSets["Biceps"] ?: 0)
        val legSets = (muscleSets["Quads"] ?: 0) + (muscleSets["Hamstrings"] ?: 0) + (muscleSets["Calves"] ?: 0)

        var score = 100
        var warning = "Great balance! Keep it up."

        if (pushSets > pullSets * 2 && pullSets > 0) {
            score -= 30
            warning = "Warning: Your push volume is much higher than pull. Add more Back/Biceps exercises!"
        } else if (pullSets > pushSets * 2 && pushSets > 0) {
            score -= 30
            warning = "Warning: Your pull volume is much higher than push. Add more Chest/Triceps exercises!"
        } else if (legSets < (pushSets + pullSets) / 4) {
            score -= 40
            warning = "Warning: You might be skipping leg day! Increase leg volume."
        }

        return Pair(score.coerceIn(0, 100), warning)
    }
}
