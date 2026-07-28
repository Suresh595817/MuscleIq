package com.example.muscleiq.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muscleiq.ui.theme.*

data class NotificationItem(
    val id: Int,
    val title: String,
    val desc: String,
    val time: String,
    val icon: ImageVector,
    val iconTint: Color,
    val bgTint: Color
)

@Composable
fun NotificationsScreen(
    onNavigateBack: () -> Unit
) {
    val notifications = listOf(
        NotificationItem(
            id = 1,
            title = "Workout Complete!",
            desc = "You crushed your Leg Day workout.",
            time = "2 hours ago",
            icon = Icons.Default.Star,
            iconTint = Accent,
            bgTint = Accent.copy(alpha = 0.2f)
        ),
        NotificationItem(
            id = 2,
            title = "New Achievement",
            desc = "Consistency is key! 3 workouts this week.",
            time = "1 day ago",
            icon = Icons.Default.Star, // Using Star instead of Trophy for now
            iconTint = Warning,
            bgTint = Warning.copy(alpha = 0.2f)
        ),
        NotificationItem(
            id = 3,
            title = "Imbalance Alert",
            desc = "Your chest volume is 40% higher than your back. Time for some rows!",
            time = "2 days ago",
            icon = Icons.Default.Favorite, // Using Favorite instead of HeartPulse for now
            iconTint = MuscleRed,
            bgTint = MuscleRed.copy(alpha = 0.2f)
        )
    )

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
                    text = "Notifications",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Notifications List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(notifications) { notification ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Dark200)
                            .border(1.dp, Dark300, RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(notification.bgTint),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                notification.icon,
                                contentDescription = null,
                                tint = notification.iconTint,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = notification.title,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = notification.desc,
                                color = Color(0xFF9CA3AF),
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = notification.time,
                                color = Color(0xFF6B7280),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
