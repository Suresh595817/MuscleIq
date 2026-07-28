package com.example.muscleiq.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.muscleiq.ui.components.MuscleHeatmap
import com.example.muscleiq.ui.theme.*
import com.example.muscleiq.ui.viewmodel.MuscleMapViewModel
import com.example.muscleiq.ui.viewmodel.MuscleStatus

@Composable
fun MuscleMapScreen(
    onNavigateBack: () -> Unit,
    viewModel: MuscleMapViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    val getMuscleColor = { muscle: String ->
        when (viewModel.getMuscleScore(muscle).status) {
            MuscleStatus.BALANCED -> MuscleGreen
            MuscleStatus.UNDERTRAINED -> MuscleYellow
            MuscleStatus.NEGLECTED -> MuscleRed
            MuscleStatus.OVERTRAINED -> MuscleOvertrained
            MuscleStatus.UNKNOWN -> Dark300
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 48.dp, start = 16.dp, end = 24.dp, bottom = 16.dp)
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Muscle Intelligence", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("AI analysis based on your recent volume.", color = Color(0xFF9CA3AF), fontSize = 14.sp)
                }
            }

            // View Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Dark200)
                    .border(1.dp, Dark300, RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (state.view == "front") Dark300 else Color.Transparent)
                        .clickable { viewModel.setView("front") }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Front", color = if (state.view == "front") Color.White else Color(0xFF6B7280), fontWeight = FontWeight.Medium)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (state.view == "back") Dark300 else Color.Transparent)
                        .clickable { viewModel.setView("back") }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Back", color = if (state.view == "back") Color.White else Color(0xFF6B7280), fontWeight = FontWeight.Medium)
                }
            }

            // Heatmap Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // Legend
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LegendItem("Balanced", MuscleGreen)
                    LegendItem("Undertrained", MuscleYellow)
                    LegendItem("Neglected", MuscleRed)
                    LegendItem("Overtrained", MuscleOvertrained)
                }

                // The SVG Canvas translation
                MuscleHeatmap(
                    viewMode = state.view,
                    getMuscleColor = getMuscleColor,
                    onMuscleClick = { muscle -> viewModel.selectMuscle(muscle) },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(bottom = 60.dp)
                        .width(280.dp)
                        .aspectRatio(0.5f)
                )
            }
        }

        // Detail Bottom Sheet
        AnimatedVisibility(
            visible = state.selectedMuscle != null,
            enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(300)),
            exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(300)),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            state.selectedMuscle?.let { muscle ->
                val scoreData = viewModel.getMuscleScore(muscle)
                
                val statusIcon = when (scoreData.status) {
                    MuscleStatus.BALANCED -> Icons.Default.CheckCircle
                    MuscleStatus.UNDERTRAINED -> Icons.Default.Refresh
                    MuscleStatus.NEGLECTED, MuscleStatus.OVERTRAINED -> Icons.Default.Warning
                    else -> Icons.Default.Info
                }
                val statusColor = getMuscleColor(muscle)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .background(Dark200)
                        .border(1.dp, Dark300, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .padding(24.dp)
                ) {
                    Column {
                        // Drag handle
                        Box(
                            modifier = Modifier
                                .width(48.dp)
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(Dark300)
                                .align(Alignment.CenterHorizontally)
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text(muscle, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                                    Icon(statusIcon, contentDescription = null, tint = statusColor, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(scoreData.status.name.lowercase().capitalize(), color = statusColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text("${scoreData.score}", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                                    Text("/100", color = Color(0xFF6B7280), fontSize = 14.sp, modifier = Modifier.padding(bottom = 6.dp))
                                }
                                Text("Recovery Score", color = Color(0xFF6B7280), fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Dark300.copy(alpha = 0.5f))
                                .border(1.dp, Dark300, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Text(scoreData.message, color = Color(0xFF9CA3AF), fontSize = 14.sp)
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { viewModel.selectMuscle(null) },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Dark300),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Close", color = Color.White, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LegendItem(text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
}
