package com.example.muscleiq.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.muscleiq.data.model.Exercise
import com.example.muscleiq.data.model.ExerciseSet
import com.example.muscleiq.data.model.Workout
import com.example.muscleiq.data.repository.AuthRepository
import com.example.muscleiq.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

data class WorkoutScreenState(
    val workoutName: String = "",
    val durationMinutes: String = "",
    val exercises: List<Exercise> = emptyList(),
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
)

class WorkoutViewModel(
    private val workoutRepository: WorkoutRepository = WorkoutRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(WorkoutScreenState())
    val state: StateFlow<WorkoutScreenState> = _state.asStateFlow()

    fun updateWorkoutName(name: String) {
        _state.update { it.copy(workoutName = name) }
    }

    fun updateDuration(duration: String) {
        _state.update { it.copy(durationMinutes = duration) }
    }

    fun addExercise(name: String, primaryMuscle: String) {
        val newExercise = Exercise(exerciseName = name, primaryMuscle = primaryMuscle)
        _state.update { it.copy(exercises = it.exercises + newExercise) }
    }

    fun addSetToExercise(exerciseIndex: Int, reps: String, weight: String) {
        val repsInt = reps.toIntOrNull() ?: 0
        val weightDouble = weight.toDoubleOrNull() ?: 0.0
        val newSet = ExerciseSet(reps = repsInt, weight = weightDouble)

        _state.update { currentState ->
            val updatedExercises = currentState.exercises.toMutableList()
            if (exerciseIndex in updatedExercises.indices) {
                val exercise = updatedExercises[exerciseIndex]
                updatedExercises[exerciseIndex] = exercise.copy(sets = exercise.sets + newSet)
            }
            currentState.copy(exercises = updatedExercises)
        }
    }
    fun updateSet(exerciseIndex: Int, setIndex: Int, weight: Double, reps: Int) {
        _state.update { currentState ->
            val updatedExercises = currentState.exercises.toMutableList()
            if (exerciseIndex in updatedExercises.indices) {
                val exercise = updatedExercises[exerciseIndex]
                val updatedSets = exercise.sets.toMutableList()
                if (setIndex in updatedSets.indices) {
                    updatedSets[setIndex] = updatedSets[setIndex].copy(weight = weight, reps = reps)
                    updatedExercises[exerciseIndex] = exercise.copy(sets = updatedSets)
                }
            }
            currentState.copy(exercises = updatedExercises)
        }
    }
    
    fun removeSetFromExercise(exerciseIndex: Int, setIndex: Int) {
        _state.update { currentState ->
            val updatedExercises = currentState.exercises.toMutableList()
            if (exerciseIndex in updatedExercises.indices) {
                val exercise = updatedExercises[exerciseIndex]
                val updatedSets = exercise.sets.toMutableList()
                if (setIndex in updatedSets.indices) {
                    updatedSets.removeAt(setIndex)
                    updatedExercises[exerciseIndex] = exercise.copy(sets = updatedSets)
                }
            }
            currentState.copy(exercises = updatedExercises)
        }
    }

    fun saveWorkout() {
        val currentState = _state.value
        val finalWorkoutName = if (currentState.workoutName.isBlank()) {
            "My Workout"
        } else {
            currentState.workoutName
        }
        
        val duration = currentState.durationMinutes.toIntOrNull() ?: 0
        val userId = authRepository.getCurrentUserId() ?: return

        val workout = Workout(
            id = UUID.randomUUID().toString(),
            userId = userId,
            name = finalWorkoutName,
            durationMinutes = duration,
            date = Date(),
            exercises = currentState.exercises
        )

        _state.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch {
            val result = workoutRepository.saveWorkout(workout)
            result.onSuccess {
                _state.update { it.copy(isSaving = false, saveSuccess = true) }
            }.onFailure { error ->
                _state.update { it.copy(isSaving = false, error = error.localizedMessage) }
            }
        }
    }

    fun resetState() {
        _state.value = WorkoutScreenState()
    }
}
