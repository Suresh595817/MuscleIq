package com.example.muscleiq.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.muscleiq.data.GeneratedExercise
import com.example.muscleiq.ui.theme.Accent
import com.example.muscleiq.ui.theme.Dark200
import com.example.muscleiq.ui.theme.Dark300
import com.example.muscleiq.ui.theme.DarkBackground
import com.example.muscleiq.ui.viewmodel.AiWorkoutState
import com.example.muscleiq.ui.viewmodel.AiWorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiGeneratorScreen(
    onNavigateBack: () -> Unit,
    viewModel: AiWorkoutViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var selectedTime by remember { mutableStateOf("30 mins") }
    var selectedEquipment by remember { mutableStateOf("Dumbbells") }
    var selectedFocus by remember { mutableStateOf("Full Body") }
    var customRequest by remember { mutableStateOf("") }

    val times = listOf("15 mins", "30 mins", "45 mins", "60 mins")
    val equipment = listOf("Bodyweight", "Dumbbells", "Barbell", "Full Gym")
    val focusAreas = listOf("Full Body", "Upper Body", "Lower Body", "Core")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp, top = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Dark200)
                        .border(1.dp, Dark300, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Accent,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI Workout",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (uiState is AiWorkoutState.Idle || uiState is AiWorkoutState.Error) {
                // Input Form
                LazyColumn(modifier = Modifier.weight(1f)) {
                    item {
                        SelectionSection("Time Available", times, selectedTime) { selectedTime = it }
                        SelectionSection("Equipment", equipment, selectedEquipment) { selectedEquipment = it }
                        SelectionSection("Focus Area", focusAreas, selectedFocus) { selectedFocus = it }
                        
                        Text(
                            text = "Any specific requests? (Optional)",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        OutlinedTextField(
                            value = customRequest,
                            onValueChange = { customRequest = it },
                            placeholder = { Text("e.g., I have a bad knee, no squats...") },
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Accent,
                                unfocusedBorderColor = Dark300,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Dark200,
                                unfocusedContainerColor = Dark200
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        if (uiState is AiWorkoutState.Error) {
                            Text(
                                text = (uiState as AiWorkoutState.Error).message,
                                color = Color.Red,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        viewModel.generateWorkout(selectedTime, selectedEquipment, selectedFocus, customRequest)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Accent),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generate My Workout", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            } else if (uiState is AiWorkoutState.Loading) {
                // Loading State
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Accent)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Gemini is building your workout...", color = Color(0xFF9CA3AF))
                    }
                }
            } else if (uiState is AiWorkoutState.Success) {
                val workout = (uiState as AiWorkoutState.Success).workout
                
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = workout.title,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = workout.description,
                        color = Color(0xFF9CA3AF),
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                    
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(workout.exercises) { exercise ->
                            ExerciseCard(exercise)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { viewModel.reset() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Dark200),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Start Over", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SelectionSection(title: String, options: List<String>, selected: String, onSelect: (String) -> Unit) {
    Text(
        text = title,
        color = Color.White,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 12.dp)
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            val isSelected = option == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) Accent else Dark200)
                    .border(1.dp, if (isSelected) Accent else Dark300, RoundedCornerShape(8.dp))
                    .clickable { onSelect(option) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    color = if (isSelected) Color.White else Color(0xFF9CA3AF),
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun ExerciseCard(exercise: GeneratedExercise) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Dark200)
            .border(1.dp, Dark300, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = exercise.name,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${exercise.sets} Sets × ${exercise.reps}",
                    color = Accent,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Rest: ${exercise.restTime}",
                    color = Color(0xFF9CA3AF),
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkBackground)
                    .padding(12.dp)
            ) {
                Text(
                    text = "💡 Tip: ${exercise.tip}",
                    color = Color(0xFF9CA3AF),
                    fontSize = 14.sp
                )
            }
        }
    }
}
