package com.example.muscleiq.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.muscleiq.ui.components.MuscleHeatmap
import com.example.muscleiq.ui.theme.*
import com.example.muscleiq.ui.viewmodel.DashboardState
import com.example.muscleiq.ui.viewmodel.DashboardViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    onNavigateToWorkout: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToMuscleMap: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAiWorkout: () -> Unit,
    onNavigateToAiDiet: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    viewModel: DashboardViewModel = viewModel()
) {
    val state by viewModel.dashboardState.collectAsState()
    val today = SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(Date())

    LaunchedEffect(Unit) {
        viewModel.loadDashboardData()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 100.dp)
        ) {
            // Greeting
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = today,
                            color = Color(0xFF9CA3AF),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        val firstName = if (state is DashboardState.Success) {
                            (state as DashboardState.Success).user?.name?.substringBefore(" ")?.takeIf { it.isNotBlank() } ?: "Athlete"
                        } else {
                            "Athlete"
                        }
                        Text(
                            text = "Ready to crush it, $firstName?",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(Dark200)
                            .border(1.dp, Dark300, androidx.compose.foundation.shape.CircleShape)
                            .clickable { onNavigateToProfile() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color(0xFF9CA3AF), modifier = Modifier.size(20.dp))
                    }
                }
            }

            // Quick Stats Grid
            item {
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp).height(IntrinsicSize.Max), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Workouts Card
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Dark200)
                            .border(1.dp, Dark300, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = Warning, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Workouts", color = Warning, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            }
                            if (state is DashboardState.Success) {
                                Text("${(state as DashboardState.Success).recentWorkouts.size}", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                                Text("Recent sessions", color = Color(0xFF9CA3AF), fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                            } else {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                            }
                        }
                    }

                    // Balance Card
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Dark200)
                            .border(1.dp, Dark300, RoundedCornerShape(16.dp))
                            .clickable { onNavigateToMuscleMap() }
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = Accent, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Balance", color = Accent, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            }
                            if (state is DashboardState.Success) {
                                Row(modifier = Modifier.fillMaxWidth().height(60.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                                    MuscleHeatmap(
                                        viewMode = "front",
                                        getMuscleColor = { Dark300 },
                                        onMuscleClick = {},
                                        modifier = Modifier.weight(1f)
                                    )
                                    MuscleHeatmap(
                                        viewMode = "back",
                                        getMuscleColor = { Dark300 },
                                        onMuscleClick = {},
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                Text("Full Map", color = Color(0xFF9CA3AF), fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                            } else {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                            }
                        }
                    }
                }
            }

            // Start Workout CTA
            item {
                Button(
                    onClick = onNavigateToWorkout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .shadow(16.dp, RoundedCornerShape(16.dp), spotColor = Accent)
                        .padding(bottom = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Accent),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Start New Workout", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            // AI Workout CTA
            item {
                Button(
                    onClick = onNavigateToAiWorkout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(bottom = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Dark200),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Accent)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Accent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AI Workout", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            // AI Diet CTA
            item {
                Button(
                    onClick = onNavigateToAiDiet,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(bottom = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Dark200),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4CAF50)) // Green border
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF4CAF50))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AI Diet Plan", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            // Analytics CTA
            item {
                Button(
                    onClick = onNavigateToAnalytics,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(bottom = 32.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Dark200),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF673AB7)) // Purple border
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF673AB7))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("View Analytics & Progress", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            // Daily Progress Bar Graph
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Dark200)
                        .border(1.dp, Dark300, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text("Progress", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().height(160.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Y-Axis Labels
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(end = 12.dp, bottom = 24.dp), // bottom padding offsets to match day labels height
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.End
                        ) {
                            Text("9", color = Color(0xFF9CA3AF), fontSize = 12.sp)
                            Text("6", color = Color(0xFF9CA3AF), fontSize = 12.sp)
                            Text("3", color = Color(0xFF9CA3AF), fontSize = 12.sp)
                            Text("0", color = Color(0xFF9CA3AF), fontSize = 12.sp)
                        }
                        
                        // Graph Area
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                            val workoutCounts = FloatArray(7) { 0f }
                            
                            if (state is DashboardState.Success) {
                                val workouts = (state as DashboardState.Success).recentWorkouts
                                val currentCalendar = Calendar.getInstance()
                                // Set first day of week to Monday for consistent week calculation
                                currentCalendar.firstDayOfWeek = Calendar.MONDAY
                                val currentWeekOfYear = currentCalendar.get(Calendar.WEEK_OF_YEAR)
                                val currentYear = currentCalendar.get(Calendar.YEAR)
                                
                                workouts.forEach { workout ->
                                    val workoutCalendar = Calendar.getInstance().apply { 
                                        firstDayOfWeek = Calendar.MONDAY
                                        time = workout.date 
                                    }
                                    if (workoutCalendar.get(Calendar.WEEK_OF_YEAR) == currentWeekOfYear &&
                                        workoutCalendar.get(Calendar.YEAR) == currentYear) {
                                        val dayOfWeek = workoutCalendar.get(Calendar.DAY_OF_WEEK)
                                        val index = if (dayOfWeek == Calendar.SUNDAY) 6 else dayOfWeek - 2
                                        if (index in 0..6) {
                                            workoutCounts[index] += 1f
                                        }
                                    }
                                }
                            }
                            
                            // Scale against max of 9
                            val progress = workoutCounts.map { (it / 9f).coerceIn(0f, 1f) }
                            
                            days.forEachIndexed { index, day ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxHeight()
                                ) {
                                    // The space for the bar
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .width(28.dp),
                                        contentAlignment = Alignment.BottomCenter
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .fillMaxHeight(progress[index].coerceAtLeast(0.02f))
                                                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                                .background(if (progress[index] > 0f) Accent else Dark300)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    // The Day label
                                    Text(
                                        text = day,
                                        color = Color(0xFF9CA3AF),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Recent Workouts Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Recent Workouts", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Text(
                        "View all", 
                        color = Color(0xFF9CA3AF), 
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { onNavigateToHistory() }
                    )
                }
            }

            if (state is DashboardState.Error) {
                item {
                    Text(
                        text = "Error: ${(state as DashboardState.Error).message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }

            // Recent Workouts List
            if (state is DashboardState.Success) {
                val workouts = (state as DashboardState.Success).recentWorkouts
                if (workouts.isEmpty()) {
                    item {
                        Text(
                            "No recent workouts.",
                            color = Color(0xFF9CA3AF),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                } else {
                    items(workouts.take(5)) { workout ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Dark200)
                                .border(1.dp, Dark300, RoundedCornerShape(12.dp))
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(workout.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("${workout.exercises.size} exercises", color = Color(0xFF9CA3AF), fontSize = 12.sp)
                                }
                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF9CA3AF))
                            }
                        }
                    }
                }
            }
        }
    }
}
