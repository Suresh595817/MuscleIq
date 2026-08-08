package com.example.muscleiq.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muscleiq.ui.theme.*

@Composable
fun SubscriptionScreen(
    onNavigateBack: () -> Unit
) {
    val features = listOf(
        "Advanced Muscle Heatmaps",
        "Unlimited Workout History",
        "AI-Powered Routine Suggestions",
        "Export Data to CSV",
        "Priority Support"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
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
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Subscription",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Hero Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Accent.copy(alpha = 0.2f), Dark200)
                            )
                        )
                        .border(1.dp, Accent.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                        .padding(24.dp)
                ) {
                    // Faint Star Background
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.05f),
                        modifier = Modifier
                            .size(120.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = 24.dp, y = (-24).dp)
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Accent)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Build, contentDescription = null, tint = Color.Black, modifier = Modifier.size(32.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("MuscleIQ ", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
                            Text("PRO", color = Accent, fontSize = 28.sp, fontWeight = FontWeight.Black)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "You are currently on the free tier. Upgrade to unlock advanced AI analytics!",
                            color = Color(0xFFD1D5DB),
                            fontSize = 14.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Features List
            item {
                Text(
                    text = "Pro Features",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    features.forEach { feature ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Dark200)
                                .border(1.dp, Dark300, RoundedCornerShape(16.dp))
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Accent, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(feature, color = Color(0xFFE5E7EB), fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}
