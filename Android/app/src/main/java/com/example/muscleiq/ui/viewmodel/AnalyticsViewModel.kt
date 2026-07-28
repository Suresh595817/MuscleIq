package com.example.muscleiq.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.muscleiq.data.StrengthRecord
import com.example.muscleiq.data.VolumeRecord
import com.example.muscleiq.data.repository.AuthRepository
import com.example.muscleiq.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AnalyticsViewModel(
    private val workoutRepository: WorkoutRepository = WorkoutRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {
    
    private val _strengthData = MutableStateFlow<List<StrengthRecord>>(emptyList())
    val strengthData: StateFlow<List<StrengthRecord>> = _strengthData.asStateFlow()

    private val _volumeData = MutableStateFlow<List<VolumeRecord>>(emptyList())
    val volumeData: StateFlow<List<VolumeRecord>> = _volumeData.asStateFlow()
    
    private val _totalWorkouts = MutableStateFlow(0)
    val totalWorkouts: StateFlow<Int> = _totalWorkouts.asStateFlow()
    
    private val _currentStreak = MutableStateFlow(0)
    val currentStreak: StateFlow<Int> = _currentStreak.asStateFlow()
    
    private val _totalWeightThisMonth = MutableStateFlow(0)
    val totalWeightThisMonth: StateFlow<Int> = _totalWeightThisMonth.asStateFlow()

    init {
        loadAnalyticsData()
    }

    private fun loadAnalyticsData() {
        val userId = authRepository.getCurrentUserId() ?: return
        
        viewModelScope.launch {
            // Load all workouts for the user
            val result = workoutRepository.getWorkoutsForUser(userId)
            if (result.isSuccess) {
                val workouts = result.getOrNull() ?: emptyList()
                
                _totalWorkouts.value = workouts.size
                
                // Calculate streak
                var streak = 0
                val calendar = Calendar.getInstance()
                
                // Very basic streak logic: check consecutive days backward from today
                val dates = workouts.map { 
                    val cal = Calendar.getInstance().apply { time = it.date }
                    "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.DAY_OF_YEAR)}"
                }.toSet()
                
                for (i in 0..365) {
                    val dateString = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.DAY_OF_YEAR)}"
                    if (dates.contains(dateString)) {
                        streak++
                        calendar.add(Calendar.DAY_OF_YEAR, -1)
                    } else if (i == 0) {
                        // It's fine if they haven't worked out today, check yesterday
                        calendar.add(Calendar.DAY_OF_YEAR, -1)
                    } else {
                        break
                    }
                }
                _currentStreak.value = streak

                // Calculate total weight this month
                var monthlyWeight = 0f
                val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
                val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                
                workouts.forEach { workout ->
                    val cal = Calendar.getInstance().apply { time = workout.date }
                    if (cal.get(Calendar.MONTH) == currentMonth && cal.get(Calendar.YEAR) == currentYear) {
                        workout.exercises.forEach { exercise ->
                            exercise.sets.forEach { set ->
                                monthlyWeight += (set.weight * set.reps).toFloat()
                            }
                        }
                    }
                }
                _totalWeightThisMonth.value = monthlyWeight.toInt()

                // Strength Data (Mock Deadlift 1RM for now until we build 1RM calculator, but tied to their actual workout dates)
                // In reality, this would filter workouts for "Deadlift" and calculate estimated 1RM.
                val strengthRecords = mutableListOf<StrengthRecord>()
                val monthFormat = SimpleDateFormat("MMM", Locale.getDefault())
                
                if (workouts.isEmpty()) {
                    _strengthData.value = emptyList()
                } else {
                    // Let's actually pull maximum weight lifted in any exercise per month as a substitute
                    val maxWeightPerMonth = mutableMapOf<String, Float>()
                    workouts.forEach { workout ->
                        val month = monthFormat.format(workout.date)
                        var maxInWorkout = 0f
                        workout.exercises.forEach { exercise ->
                            exercise.sets.forEach { set ->
                                if (set.weight > maxInWorkout) {
                                    maxInWorkout = set.weight.toFloat()
                                }
                            }
                        }
                        if (maxInWorkout > (maxWeightPerMonth[month] ?: 0f)) {
                            maxWeightPerMonth[month] = maxInWorkout
                        }
                    }
                    maxWeightPerMonth.forEach { (month, weight) ->
                        strengthRecords.add(StrengthRecord(month, weight))
                    }
                    _strengthData.value = strengthRecords.reversed() // Oldest first
                }

                // Volume Data (Current week)
                val volumeRecords = FloatArray(7) { 0f }
                val currentWeek = Calendar.getInstance().apply { firstDayOfWeek = Calendar.MONDAY }.get(Calendar.WEEK_OF_YEAR)
                val year = Calendar.getInstance().get(Calendar.YEAR)
                
                workouts.forEach { workout ->
                    val cal = Calendar.getInstance().apply { 
                        firstDayOfWeek = Calendar.MONDAY
                        time = workout.date 
                    }
                    
                    if (cal.get(Calendar.WEEK_OF_YEAR) == currentWeek && cal.get(Calendar.YEAR) == year) {
                        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
                        val index = if (dayOfWeek == Calendar.SUNDAY) 6 else dayOfWeek - 2
                        if (index in 0..6) {
                            var volume = 0f
                            workout.exercises.forEach { exercise ->
                                exercise.sets.forEach { set ->
                                    volume += (set.weight * set.reps).toFloat()
                                }
                            }
                            volumeRecords[index] += volume
                        }
                    }
                }
                
                val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                val finalVolumeData = days.mapIndexed { index, day ->
                    VolumeRecord(day, volumeRecords[index])
                }
                _volumeData.value = finalVolumeData
            }
        }
    }
}
