package com.example.muscleiq.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muscleiq.ui.theme.*
import com.example.muscleiq.data.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    onNavigateToSubscription: () -> Unit,
    onNavigateToHelpCenter: () -> Unit,
    onNavigateToRateApp: () -> Unit,
    onNavigateToAuth: () -> Unit,
    user: User? = null // Passed down if available, or fetched
) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var weeklyReportsEnabled by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

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
                        .padding(top = 24.dp, bottom = 32.dp),
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
                    Text(
                        text = "Settings",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // App Preferences Section
            item {
                Text("Preferences", color = Color(0xFF9CA3AF), fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp, start = 4.dp))
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Dark200)
                        .border(1.dp, Dark300, RoundedCornerShape(16.dp))
                ) {
                    SettingsToggleItem(
                        icon = Icons.Default.Notifications,
                        iconTint = Warning, // Yellow
                        title = "Push Notifications",
                        subtitle = "Daily reminders & updates",
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it }
                    )
                    HorizontalDivider(color = Dark300, modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsToggleItem(
                        icon = Icons.Default.Email,
                        iconTint = Accent, // Green
                        title = "Weekly Reports",
                        subtitle = "Get your progress via email",
                        checked = weeklyReportsEnabled,
                        onCheckedChange = { weeklyReportsEnabled = it }
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Account Section
            item {
                Text("Account", color = Color(0xFF9CA3AF), fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp, start = 4.dp))
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Dark200)
                        .border(1.dp, Dark300, RoundedCornerShape(16.dp))
                ) {
                    SettingsActionItem(
                        icon = Icons.Default.Person,
                        iconTint = Accent,
                        title = "Edit Profile",
                        onClick = onNavigateToEditProfile
                    )
                    HorizontalDivider(color = Dark300, modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsActionItem(
                        icon = Icons.Default.Lock,
                        iconTint = Warning,
                        title = "Change Password",
                        onClick = onNavigateToChangePassword
                    )
                    HorizontalDivider(color = Dark300, modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsActionItem(
                        icon = Icons.Default.ShoppingCart, // Subscription proxy icon
                        iconTint = Color(0xFFF472B6), // Pink
                        title = "Subscription & Billing",
                        onClick = onNavigateToSubscription
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Support & About
            item {
                Text("Support & About", color = Color(0xFF9CA3AF), fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp, start = 4.dp))
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Dark200)
                        .border(1.dp, Dark300, RoundedCornerShape(16.dp))
                ) {
                    SettingsActionItem(
                        icon = Icons.Default.Info,
                        iconTint = Color(0xFF60A5FA), // Blue
                        title = "Help Center",
                        onClick = onNavigateToHelpCenter
                    )
                    HorizontalDivider(color = Dark300, modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsActionItem(
                        icon = Icons.Default.Star,
                        iconTint = Warning,
                        title = "Rate the App",
                        onClick = onNavigateToRateApp
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
            
            // Danger Zone
            item {
                Button(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    border = BorderStroke(1.dp, MuscleRed.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = MuscleRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete Account", color = MuscleRed, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                
                Text(
                    text = "App Version 1.0.0",
                    color = Color(0xFF6B7280),
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
        
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                containerColor = Dark200,
                title = {
                    Text("Delete Account", color = Color.White, fontWeight = FontWeight.Bold)
                },
                text = {
                    Text(
                        "Are you sure you want to delete your account? This action cannot be undone and you will lose all your workout history.",
                        color = Color(0xFF9CA3AF)
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            onNavigateToAuth()
                        }
                    ) {
                        Text("Delete", color = MuscleRed, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel", color = Color.White)
                    }
                }
            )
        }
    }
}

@Composable
fun SettingsToggleItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Text(subtitle, color = Color(0xFF9CA3AF), fontSize = 12.sp)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Accent,
                uncheckedThumbColor = Color(0xFF9CA3AF),
                uncheckedTrackColor = Dark300
            )
        )
    }
}

@Composable
fun SettingsActionItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color(0xFF6B7280))
    }
}
