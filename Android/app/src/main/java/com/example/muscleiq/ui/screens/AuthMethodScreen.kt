package com.example.muscleiq.ui.screens

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.muscleiq.R
import com.example.muscleiq.ui.theme.Accent
import com.example.muscleiq.ui.theme.Dark200
import com.example.muscleiq.ui.theme.Dark300
import com.example.muscleiq.ui.theme.DarkBackground
import com.example.muscleiq.ui.viewmodel.AuthState
import com.example.muscleiq.ui.viewmodel.AuthViewModel

@Composable
fun AuthMethodScreen(
    onNavigateToDashboard: () -> Unit,
    onNavigateToEmailAuth: () -> Unit,
    onNavigateToPhoneAuth: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onNavigateToDashboard()
            viewModel.resetState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.muscleiq_logo),
                contentDescription = "MuscleIQ Logo",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(2.dp, Accent, RoundedCornerShape(24.dp))
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Welcome to MuscleIQ",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Log in or sign up to continue",
                color = Color(0xFF9CA3AF),
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 48.dp),
                textAlign = TextAlign.Center
            )
            
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(color = Accent)
                Spacer(modifier = Modifier.height(24.dp))
            } else {
                AuthOptionButton(
                    text = "Continue with Google",
                    icon = {
                        // Dummy icon for Google
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("G", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    },
                    onClick = {
                        val activity = context as? Activity
                        if (activity != null) {
                            viewModel.signInWithGoogle(activity)
                        } else {
                            Log.e("AuthMethodScreen", "Context is not an Activity")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                AuthOptionButton(
                    text = "Continue with Email",
                    icon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color.White) },
                    onClick = onNavigateToEmailAuth
                )
                
                if (authState is AuthState.Error) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = (authState as AuthState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun AuthOptionButton(
    text: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Dark200)
            .border(1.dp, Dark300, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            icon()
            Spacer(modifier = Modifier.width(16.dp))
            Text(text, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}
