package com.example.muscleiq.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.muscleiq.R
import com.example.muscleiq.data.model.ExerciseCatalog
import com.example.muscleiq.ui.theme.*
import com.example.muscleiq.ui.viewmodel.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen(
    onNavigateBack: () -> Unit,
    viewModel: WorkoutViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    var showExerciseSheet by remember { mutableStateOf(false) }
    var selectedMuscleGroup by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            onNavigateBack()
            viewModel.resetState()
        }
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    LaunchedEffect(state.error) {
        state.error?.let {
            android.widget.Toast.makeText(context, it, android.widget.Toast.LENGTH_LONG).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        if (showExerciseSheet) {
            // Full Screen Visual Muscle Catalog
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp, top = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { 
                        if (selectedMuscleGroup != null) {
                            selectedMuscleGroup = null
                        } else {
                            showExerciseSheet = false 
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = if (selectedMuscleGroup == null) "Select Muscle Group" else selectedMuscleGroup!!,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (selectedMuscleGroup == null) {
                    // Muscle Group Grid
                    val muscles = listOf(
                        "Chest" to R.drawable.muscle_chest,
                        "Back" to R.drawable.muscle_back,
                        "Legs" to R.drawable.muscle_legs,
                        "Shoulders" to R.drawable.muscle_shoulders,
                        "Biceps" to R.drawable.muscle_biceps,
                        "Triceps" to R.drawable.muscle_triceps,
                        "Core" to R.drawable.muscle_core,
                        "Forearms" to R.drawable.muscle_forearms
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(muscles) { (muscleName, imageRes) ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable { selectedMuscleGroup = muscleName }
                            ) {
                                Image(
                                    painter = painterResource(id = imageRes),
                                    contentDescription = muscleName,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                // Gradient Overlay
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                                                startY = 100f
                                            )
                                        )
                                )
                                Text(
                                    text = muscleName,
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(16.dp)
                                )
                            }
                        }
                    }
                } else {
                    // Specific Exercise List
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        val exercises = ExerciseCatalog.catalog[selectedMuscleGroup] ?: emptyList()
                        items(exercises) { exerciseName ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { 
                                        viewModel.addExercise(exerciseName, selectedMuscleGroup!!)
                                        showExerciseSheet = false
                                        selectedMuscleGroup = null
                                    }
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(exerciseName, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                                Icon(Icons.Default.Add, contentDescription = "Add", tint = Accent)
                            }
                            HorizontalDivider(color = Dark300, modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }
            }
        } else {
            // Main Workout Logging UI with Hero Image
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Parallax Hero Section
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.workout_hero),
                            contentDescription = "Workout Hero",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        // Gradient Overlay to blend with background
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Black.copy(alpha = 0.3f),
                                            Color.Black.copy(alpha = 0.6f),
                                            DarkBackground
                                        ),
                                        startY = 0f
                                    )
                                )
                        )
                        
                        // Header Content
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 48.dp, start = 24.dp, end = 24.dp, bottom = 24.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(onClick = onNavigateBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                            }
                            
                            BasicTextField(
                                value = state.workoutName,
                                onValueChange = { viewModel.updateWorkoutName(it) },
                                textStyle = TextStyle(
                                    color = Color.White,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.ExtraBold
                                ),
                                cursorBrush = SolidColor(Color.White),
                                modifier = Modifier.fillMaxWidth(),
                                decorationBox = { innerTextField ->
                                    if (state.workoutName.isEmpty()) {
                                        Text("Workout Name", color = Color.White.copy(alpha = 0.5f), fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                                    }
                                    innerTextField()
                                }
                            )
                        }
                    }
                }

                // Exercises List
                itemsIndexed(state.exercises) { exerciseIndex, exercise ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Dark200.copy(alpha = 0.8f)) // Glassmorphism-like
                            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(exercise.exerciseName, color = Accent, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Grid Header
                            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp)) {
                                Text("SET", modifier = Modifier.weight(0.5f), color = Color(0xFF9CA3AF), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("KG", modifier = Modifier.weight(1f), color = Color(0xFF9CA3AF), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("REPS", modifier = Modifier.weight(1f), color = Color(0xFF9CA3AF), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.weight(0.5f))
                            }
                            
                            // Sets loop
                            exercise.sets.forEachIndexed { setIndex, exerciseSet ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier.weight(0.5f).padding(end = 8.dp).clip(RoundedCornerShape(8.dp)).background(Dark300).padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("${setIndex + 1}", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    }
                                    
                                    var weightText by remember { mutableStateOf(if(exerciseSet.weight == 0.0) "" else exerciseSet.weight.toString()) }
                                    BasicTextField(
                                        value = weightText,
                                        onValueChange = { 
                                            weightText = it
                                            viewModel.updateSet(exerciseIndex, setIndex, it.toDoubleOrNull() ?: 0.0, exerciseSet.reps)
                                        },
                                        textStyle = TextStyle(color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f).padding(end = 8.dp).clip(RoundedCornerShape(12.dp)).background(DarkBackground).border(1.dp, Dark300, RoundedCornerShape(12.dp)).padding(vertical = 8.dp, horizontal = 12.dp)
                                    )
                                    
                                    var repsText by remember { mutableStateOf(if(exerciseSet.reps == 0) "" else exerciseSet.reps.toString()) }
                                    BasicTextField(
                                        value = repsText,
                                        onValueChange = { 
                                            repsText = it
                                            viewModel.updateSet(exerciseIndex, setIndex, exerciseSet.weight, it.toIntOrNull() ?: 0)
                                        },
                                        textStyle = TextStyle(color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f).padding(end = 8.dp).clip(RoundedCornerShape(12.dp)).background(DarkBackground).border(1.dp, Dark300, RoundedCornerShape(12.dp)).padding(vertical = 8.dp, horizontal = 12.dp)
                                    )
                                    
                                    IconButton(
                                        onClick = { viewModel.removeSetFromExercise(exerciseIndex, setIndex) },
                                        modifier = Modifier.weight(0.5f).size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete Set", tint = Color(0xFF6B7280))
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Add Set Button
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Accent.copy(alpha = 0.15f))
                                    .clickable { viewModel.addSetToExercise(exerciseIndex, "0", "0.0") }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("+ Add Set", color = Accent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Add Exercise Button (Dashed)
                item {
                    val stroke = Stroke(width = 4f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                            .height(70.dp)
                            .clickable { showExerciseSheet = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawRoundRect(color = Color.White.copy(alpha = 0.3f), style = stroke, cornerRadius = androidx.compose.ui.geometry.CornerRadius(40f, 40f))
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add New Exercise", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    // Extra spacing at bottom so it doesn't get hidden behind the floating button
                    Spacer(modifier = Modifier.height(120.dp))
                }
            }
            
            // Sticky Finish Workout Button
            if (state.exercises.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, DarkBackground, DarkBackground),
                                startY = 0f
                            )
                        )
                        .padding(24.dp)
                        .padding(top = 24.dp)
                ) {
                    Button(
                        onClick = { viewModel.saveWorkout() },
                        modifier = Modifier.fillMaxWidth().height(60.dp).shadow(24.dp, RoundedCornerShape(20.dp), spotColor = Accent),
                        colors = ButtonDefaults.buttonColors(containerColor = Accent),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        if (state.isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Done, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Finish Workout", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
