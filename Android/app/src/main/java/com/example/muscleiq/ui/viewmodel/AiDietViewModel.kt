package com.example.muscleiq.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

sealed class AiState {
    object Idle : AiState()
    object Loading : AiState()
    data class Success(val data: String) : AiState()
    data class Error(val message: String) : AiState()
}

class AiDietViewModel : ViewModel() {
    private val _dietState = MutableStateFlow<AiState>(AiState.Idle)
    val dietState: StateFlow<AiState> = _dietState.asStateFlow()

    // 180s timeout because local Llama 3 generation can be slow
    private val client = OkHttpClient.Builder()
        .connectTimeout(180, TimeUnit.SECONDS)
        .readTimeout(180, TimeUnit.SECONDS)
        .writeTimeout(180, TimeUnit.SECONDS)
        .build()

    fun generateDiet(goal: String, weight: String, preference: String) {
        _dietState.value = AiState.Loading
        
        viewModelScope.launch {
            try {
                val prompt = """
                    You are an AI Coach, an expert sports nutritionist. 
                    Create a personalized daily diet plan for a person who weighs $weight kg, wants to $goal, and has a $preference dietary preference.
                    
                    Return ONLY valid JSON in this exact format, with NO markdown formatting, NO backticks, and NO other text before or after:
                    {
                        "macros": {
                            "calories": 2500,
                            "protein": 180,
                            "carbs": 250,
                            "fats": 70
                        },
                        "meals": [
                            {
                                "name": "Breakfast",
                                "items": ["3 Scrambled Eggs", "2 slices whole wheat toast", "1 cup blueberries"]
                            },
                            {
                                "name": "Lunch",
                                "items": ["200g Grilled Chicken Breast", "150g Quinoa", "Broccoli"]
                            }
                        ]
                    }
                """.trimIndent()

                val jsonBody = JSONObject().apply {
                    put("model", "llama3")
                    put("prompt", prompt)
                    put("stream", false)
                }

                val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())
                
                val request = Request.Builder()
                    .url("http://127.0.0.1:11434/api/generate")
                    .post(requestBody)
                    .build()

                val response = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    client.newCall(request).execute()
                }

                if (response.isSuccessful) {
                    val responseBodyString = response.body?.string()
                    if (responseBodyString != null) {
                        val responseJson = JSONObject(responseBodyString)
                        val aiResponse = responseJson.getString("response")
                        
                        // Attempt to clean the string if Ollama still returns markdown
                        val cleanedJson = aiResponse.replace("```json", "").replace("```", "").trim()
                        
                        _dietState.value = AiState.Success(cleanedJson)
                    } else {
                        _dietState.value = AiState.Error("Empty response from AI")
                    }
                } else {
                    _dietState.value = AiState.Error("AI connection failed. Is Ollama running?")
                }
            } catch (e: Exception) {
                _dietState.value = AiState.Error(e.localizedMessage ?: "Failed to connect to local Ollama instance. Is Ollama running?")
            }
        }
    }
}
