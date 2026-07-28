package com.example.muscleiq.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.muscleiq.BuildConfig
import com.example.muscleiq.data.GeneratedWorkout
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AiWorkoutState {
    object Idle : AiWorkoutState()
    object Loading : AiWorkoutState()
    data class Success(val workout: GeneratedWorkout) : AiWorkoutState()
    data class Error(val message: String) : AiWorkoutState()
}

class AiWorkoutViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<AiWorkoutState>(AiWorkoutState.Idle)
    val uiState: StateFlow<AiWorkoutState> = _uiState

    // Initialize GenerativeModel
    private val generativeModel = GenerativeModel(
        modelName = "gemini-flash-latest",
        apiKey = "YOUR_GEMINI_API_KEY",
        generationConfig = generationConfig {
            responseMimeType = "application/json"
        }
    )

    fun generateWorkout(time: String, equipment: String, focus: String, customRequest: String) {
        _uiState.value = AiWorkoutState.Loading

        viewModelScope.launch {
            try {
                val prompt = """
                    You are an expert personal trainer. Generate a workout based on the following:
                    Time available: $time
                    Equipment available: $equipment
                    Focus area: $focus
                    Custom request: $customRequest
                    
                    Return ONLY a JSON object that matches this exact schema, with no markdown formatting or extra text:
                    {
                      "title": "A catchy title for the workout",
                      "description": "A short motivational description",
                      "exercises": [
                        {
                          "name": "Exercise Name",
                          "sets": "3",
                          "reps": "10-12",
                          "rest_time": "60s",
                          "tip": "A quick pro-tip for form"
                        }
                      ]
                    }
                """.trimIndent()

                val response = generativeModel.generateContent(
                    content { text(prompt) }
                )
                
                val responseText = response.text
                if (responseText != null) {
                    val gson = Gson()
                    val generatedWorkout = gson.fromJson(responseText, GeneratedWorkout::class.java)
                    _uiState.value = AiWorkoutState.Success(generatedWorkout)
                } else {
                    _uiState.value = AiWorkoutState.Error("Received empty response from AI.")
                }
            } catch (e: Exception) {
                _uiState.value = AiWorkoutState.Error("Error: ${e.localizedMessage}")
            }
        }
    }

    fun reset() {
        _uiState.value = AiWorkoutState.Idle
    }
}
