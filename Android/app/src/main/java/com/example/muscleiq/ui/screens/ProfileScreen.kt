package com.example.muscleiq.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.example.muscleiq.ui.theme.*
import com.example.muscleiq.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToAccountSettings: () -> Unit,
    onNavigateToSubscription: () -> Unit,
    onNavigateToAuth: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

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
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onNavigateBack, modifier = Modifier.offset(x = (-12).dp)) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Text("Profile", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Dark200)
                            .border(1.dp, Dark300, CircleShape)
                            .clickable { onNavigateToAccountSettings() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color(0xFF9CA3AF), modifier = Modifier.size(20.dp))
                    }
                }
            }

            // User Info
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(Dark200)
                            .border(2.dp, Accent, CircleShape)
                            .clickable { galleryLauncher.launch("image/*") }
                            .padding(bottom = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUri != null) {
                            AsyncImage(
                                model = imageUri,
                                contentDescription = "Profile Photo",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF9CA3AF), modifier = Modifier.size(40.dp))
                        }
                        
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = 4.dp, y = 4.dp)
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Accent)
                                .border(2.dp, DarkBackground, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(state.user?.name?.takeIf { it.isNotBlank() } ?: "Athlete", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(state.user?.email ?: "", color = Color(0xFF9CA3AF), fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Accent.copy(alpha = 0.2f))
                            .padding(horizontal = 12.dp, vertical = 2.dp)
                    ) {
                        Text(
                            "User", 
                            color = Accent, 
                            fontSize = 12.sp, 
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }

            // Stats Grid
            item {
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Total Workouts
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Dark200)
                            .border(1.dp, Dark300, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Accent.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.ThumbUp, contentDescription = null, tint = Accent, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("${state.totalWorkouts}", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Text("Total Workouts", color = Color(0xFF9CA3AF), fontSize = 12.sp)
                            }
                        }
                    }

                    // Top Exercise
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Dark200)
                            .border(1.dp, Dark300, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Warning.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Done, contentDescription = null, tint = Warning, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(state.topExercise, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                Text("Top Exercise", color = Color(0xFF9CA3AF), fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Menu Items
            item {
                Column(modifier = Modifier.padding(bottom = 32.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    MenuItem("Account Settings", onClick = onNavigateToAccountSettings)
                    MenuItem("Notifications", onClick = onNavigateToNotifications)
                    MenuItem("Subscription", onClick = onNavigateToSubscription)
                    MenuItem("Help & Support")
                }
            }

            // Logout
            item {
                Button(
                    onClick = { viewModel.logout(onSuccess = onNavigateToAuth) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MuscleRed.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null, tint = MuscleRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sign Out", color = MuscleRed, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun MenuItem(name: String, onClick: (() -> Unit)? = null) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Dark200)
            .border(1.dp, Dark300, RoundedCornerShape(12.dp))
            .clickable { onClick?.invoke() }
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(name, color = Color.White, fontWeight = FontWeight.Medium)
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color(0xFF6B7280))
        }
    }
}
