package com.example.muscleiq.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.muscleiq.data.model.User
import com.example.muscleiq.data.repository.AuthRepository
import com.example.muscleiq.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileState(
    val user: User? = null,
    val totalWorkouts: Int = 0,
    val topExercise: String = "—",
    val isLoading: Boolean = true
)

class ProfileViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val workoutRepository: WorkoutRepository = WorkoutRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    fun loadProfile() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val userResult = authRepository.getCurrentUserData()
            val userId = authRepository.getCurrentUserId()
            
            var workoutsCount = 0
            var favoriteExercise = "—"
            
            if (userId != null) {
                val workoutsResult = workoutRepository.getWorkoutsForUser(userId)
                workoutsResult.onSuccess { workouts ->
                    workoutsCount = workouts.size
                    
                    // Calculate top exercise
                    val exerciseCounts = mutableMapOf<String, Int>()
                    for (workout in workouts) {
                        for (ex in workout.exercises) {
                            exerciseCounts[ex.exerciseName] = exerciseCounts.getOrDefault(ex.exerciseName, 0) + 1
                        }
                    }
                    if (exerciseCounts.isNotEmpty()) {
                        favoriteExercise = exerciseCounts.maxByOrNull { it.value }?.key ?: "—"
                    }
                }
            }

            userResult.onSuccess { user ->
                _state.update { it.copy(
                    user = user, 
                    totalWorkouts = workoutsCount,
                    topExercise = favoriteExercise,
                    isLoading = false
                ) }
            }.onFailure {
                // If fetching user fails, just stop loading for now
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
    
    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onSuccess()
        }
    }
}
