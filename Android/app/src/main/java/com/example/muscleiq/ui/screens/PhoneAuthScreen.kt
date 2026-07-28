package com.example.muscleiq.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.muscleiq.ui.theme.Accent
import com.example.muscleiq.ui.theme.Dark200
import com.example.muscleiq.ui.theme.Dark300
import com.example.muscleiq.ui.theme.DarkBackground
import com.example.muscleiq.ui.theme.MuscleRed
import com.example.muscleiq.ui.viewmodel.AuthState
import com.example.muscleiq.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneAuthScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current
    
    var phoneNumber by remember { mutableStateOf("") }
    var verificationId by remember { mutableStateOf<String?>(null) }
    var otp by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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
                    text = if (verificationId == null) "Phone Login" else "Verify OTP",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (verificationId == null) {
                // Phone Number Input
                Text(
                    text = "Enter your phone number",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "We will send you a one-time password.",
                    color = Color(0xFF9CA3AF),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(32.dp))
                
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number") },
                    placeholder = { Text("+91 9876543210") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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
                        Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF9CA3AF))
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = {
                        if (phoneNumber.isBlank()) {
                            errorMessage = "Please enter a valid phone number"
                        } else {
                            errorMessage = null
                            val formattedNumber = if (phoneNumber.startsWith("+")) phoneNumber else "+91$phoneNumber"
                            viewModel.startPhoneNumberVerification(
                                context as android.app.Activity,
                                formattedNumber,
                                onCodeSent = { vid -> verificationId = vid },
                                onError = { errorMessage = it }
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Accent),
                    shape = RoundedCornerShape(12.dp),
                    enabled = authState !is AuthState.Loading
                ) {
                    if (authState is AuthState.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Send OTP", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // OTP Input
                Text(
                    text = "Enter OTP",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Sent to $phoneNumber",
                    color = Color(0xFF9CA3AF),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(32.dp))
                
                OutlinedTextField(
                    value = otp,
                    onValueChange = { otp = it },
                    label = { Text("OTP") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (otp.isBlank()) {
                            errorMessage = "Please enter the OTP"
                        } else {
                            errorMessage = null
                            viewModel.verifyOtpAndSignIn(verificationId!!, otp)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Accent),
                    shape = RoundedCornerShape(12.dp),
                    enabled = authState !is AuthState.Loading
                ) {
                    if (authState is AuthState.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Verify & Sign In", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (errorMessage != null || authState is AuthState.Error) {
                Spacer(modifier = Modifier.height(24.dp))
                val errorMsg = errorMessage ?: (authState as? AuthState.Error)?.message ?: "An error occurred"
                Text(
                    text = errorMsg,
                    color = MuscleRed,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
