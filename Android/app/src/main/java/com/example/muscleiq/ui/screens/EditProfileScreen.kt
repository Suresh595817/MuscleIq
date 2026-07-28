package com.example.muscleiq.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muscleiq.data.repository.AuthRepository
import com.example.muscleiq.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf(false) }
    
    val authRepository = remember { AuthRepository() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val result = authRepository.getCurrentUserData()
        result.onSuccess { user ->
            name = user.name
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
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp, top = 24.dp),
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
                    text = "Edit Profile",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text("Name", color = Color(0xFF9CA3AF), fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Accent,
                    unfocusedBorderColor = Dark300,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Accent,
                    focusedContainerColor = Dark200,
                    unfocusedContainerColor = Dark200
                ),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF9CA3AF))
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
            
            if (errorMessage != null) {
                Text(errorMessage!!, color = MuscleRed, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
            }
            if (successMessage) {
                Text("Profile updated successfully!", color = Accent, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    if (name.isBlank()) {
                        errorMessage = "Name cannot be empty"
                        return@Button
                    }
                    isLoading = true
                    errorMessage = null
                    successMessage = false
                    scope.launch {
                        val result = authRepository.updateName(name)
                        result.onSuccess {
                            successMessage = true
                        }.onFailure {
                            errorMessage = it.localizedMessage ?: "Failed to update profile"
                        }
                        isLoading = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Accent),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Save Changes", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
