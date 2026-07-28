package com.example.muscleiq.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class MuscleStatus {
    BALANCED, UNDERTRAINED, NEGLECTED, OVERTRAINED, UNKNOWN
}

data class MuscleScore(
    val score: Int,
    val status: MuscleStatus,
    val message: String
)

data class MuscleMapState(
    val view: String = "front",
    val selectedMuscle: String? = null
)

class MuscleMapViewModel : ViewModel() {
    private val _state = MutableStateFlow(MuscleMapState())
    val state: StateFlow<MuscleMapState> = _state.asStateFlow()

    fun setView(newView: String) {
        _state.update { it.copy(view = newView) }
    }

    fun selectMuscle(muscle: String?) {
        _state.update { it.copy(selectedMuscle = muscle) }
    }

    // Dummy scoring for now
    fun getMuscleScore(muscle: String): MuscleScore {
        return when (muscle) {
            "Chest", "Front Delts" -> MuscleScore(85, MuscleStatus.BALANCED, "Perfectly balanced. Keep up the good work.")
            "Lats", "Upper Back" -> MuscleScore(40, MuscleStatus.UNDERTRAINED, "Slightly undertrained. Consider adding volume.")
            "Hamstrings", "Calves" -> MuscleScore(20, MuscleStatus.NEGLECTED, "Critically neglected. High risk of imbalance.")
            "Quads", "Biceps" -> MuscleScore(15, MuscleStatus.OVERTRAINED, "Overtrained. Needs recovery time.")
            else -> MuscleScore(50, MuscleStatus.UNKNOWN, "Log more workouts to get insights.")
        }
    }
}
