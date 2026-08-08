package com.example.muscleiq.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.muscleiq.ui.viewmodel.AiDietViewModel
import com.example.muscleiq.ui.viewmodel.AiState
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiDietScreen(
    onNavigateBack: () -> Unit,
    viewModel: AiDietViewModel = viewModel()
) {
    val dietState by viewModel.dietState.collectAsState()
    
    var weight by remember { mutableStateOf("80") }
    var goal by remember { mutableStateOf("Cut fat") }
    var preference by remember { mutableStateOf("No restrictions") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Diet Plan") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            
            Text("Body Weight (kg)", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text("Your Goal", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = goal,
                onValueChange = { goal = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. Bulk, Cut, Maintain") }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text("Dietary Preference", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = preference,
                onValueChange = { preference = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. Vegetarian, Keto, None") }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { viewModel.generateDiet(goal, weight, preference) },
                modifier = Modifier.fillMaxWidth(),
                enabled = dietState !is AiState.Loading && weight.isNotBlank()
            ) {
                if (dietState is AiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Generate Custom Diet Plan")
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            when (dietState) {
                is AiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Consulting AI Coach... (This may take a minute)", color = MaterialTheme.colorScheme.primary)
                    }
                }
                is AiState.Error -> {
                    Text(
                        text = (dietState as AiState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                is AiState.Success -> {
                    val jsonResponse = (dietState as AiState.Success).data
                    DietPlanView(jsonString = jsonResponse)
                }
                else -> {}
            }
        }
    }
}

@Composable
fun DietPlanView(jsonString: String) {
    var parsedMacros: JSONObject? = null
    var parsedMealsArray: org.json.JSONArray? = null
    var errorMessage: String? = null

    try {
        val json = JSONObject(jsonString)
        parsedMacros = json.getJSONObject("macros")
        parsedMealsArray = json.getJSONArray("meals")
    } catch (e: Exception) {
        errorMessage = "Could not parse AI response: ${e.localizedMessage}"
    }

    if (errorMessage != null) {
        Text(errorMessage, color = MaterialTheme.colorScheme.error)
    } else if (parsedMacros != null && parsedMealsArray != null) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Daily Targets", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Calories: ${parsedMacros.getInt("calories")} kcal")
                            Text("Protein: ${parsedMacros.getInt("protein")}g")
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Carbs: ${parsedMacros.getInt("carbs")}g")
                            Text("Fats: ${parsedMacros.getInt("fats")}g")
                        }
                    }
                }
            }
            
            items(parsedMealsArray.length()) { index ->
                val meal = parsedMealsArray.getJSONObject(index)
                val mealName = meal.getString("name")
                val itemsArray = meal.getJSONArray("items")
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(mealName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        for (i in 0 until itemsArray.length()) {
                            Text("• ${itemsArray.getString(i)}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
