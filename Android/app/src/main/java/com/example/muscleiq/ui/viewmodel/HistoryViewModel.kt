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

sealed class HistoryState {
    object Loading : HistoryState()
    data class Success(val workouts: List<Workout>) : HistoryState()
    data class Error(val message: String) : HistoryState()
}

class HistoryViewModel(
    private val workoutRepository: WorkoutRepository = WorkoutRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _state = MutableStateFlow<HistoryState>(HistoryState.Loading)
    val state: StateFlow<HistoryState> = _state.asStateFlow()

    fun loadHistory() {
        _state.value = HistoryState.Loading
        val userId = authRepository.getCurrentUserId()
        if (userId == null) {
            _state.value = HistoryState.Error("User not logged in")
            return
        }

        viewModelScope.launch {
            val result = workoutRepository.getWorkoutsForUser(userId)
            result.onSuccess { workouts ->
                _state.value = HistoryState.Success(workouts)
            }.onFailure { error ->
                _state.value = HistoryState.Error(error.localizedMessage ?: "Failed to load history")
            }
        }
    }
}
