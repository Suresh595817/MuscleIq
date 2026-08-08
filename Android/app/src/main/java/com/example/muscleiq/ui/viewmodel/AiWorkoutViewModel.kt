package com.example.muscleiq.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.muscleiq.data.GeneratedWorkout
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit
import org.json.JSONObject

sealed class AiWorkoutState {
    object Idle : AiWorkoutState()
    object Loading : AiWorkoutState()
    data class Success(val workout: GeneratedWorkout) : AiWorkoutState()
    data class Error(val message: String) : AiWorkoutState()
}

class AiWorkoutViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<AiWorkoutState>(AiWorkoutState.Idle)
    val uiState: StateFlow<AiWorkoutState> = _uiState

    // Changed to 127.0.0.1 so that 'adb reverse' works perfectly with your physical phone!
    private val ollamaUrl = "http://127.0.0.1:11434/api/generate"
    private val client = OkHttpClient.Builder()
        .connectTimeout(180, TimeUnit.SECONDS)
        .readTimeout(180, TimeUnit.SECONDS)
        .writeTimeout(180, TimeUnit.SECONDS)
        .build()

    fun generateWorkout(time: String, equipment: String, focus: String, customRequest: String) {
        _uiState.value = AiWorkoutState.Loading

        viewModelScope.launch {
            try {
                val prompt = """
                    You are an AI Coach, an expert personal trainer. Generate a workout based on the following:
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

                // Create JSON payload for Ollama
                val jsonPayload = JSONObject().apply {
                    put("model", "llama3")
                    put("prompt", prompt)
                    put("stream", false)
                    put("format", "json")
                }

                val body = jsonPayload.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
                
                val request = Request.Builder()
                    .url(ollamaUrl)
                    .post(body)
                    .build()

                // Execute HTTP Request on IO Thread
                val responseText = withContext(Dispatchers.IO) {
                    val response = client.newCall(request).execute()
                    if (!response.isSuccessful) {
                        throw Exception("Ollama HTTP Error: ${response.code}")
                    }
                    val rawBody = response.body?.string() ?: ""
                    // Parse Ollama's response object
                    val jsonResponse = JSONObject(rawBody)
                    jsonResponse.getString("response")
                }
                
                val gson = Gson()
                val generatedWorkout = gson.fromJson(responseText, GeneratedWorkout::class.java)
                _uiState.value = AiWorkoutState.Success(generatedWorkout)

            } catch (e: Exception) {
                _uiState.value = AiWorkoutState.Error("Error connecting to local AI: ${e.localizedMessage}")
            }
        }
    }

    fun reset() {
        _uiState.value = AiWorkoutState.Idle
    }
}
