package com.example.muscleiq

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.muscleiq.ui.screens.AuthScreen
import com.example.muscleiq.ui.screens.AuthMethodScreen
import com.example.muscleiq.ui.screens.PhoneAuthScreen
import com.example.muscleiq.ui.screens.DashboardScreen
import com.example.muscleiq.ui.screens.HistoryScreen
import com.example.muscleiq.ui.screens.NotificationsScreen
import com.example.muscleiq.ui.screens.AccountSettingsScreen
import com.example.muscleiq.ui.screens.SubscriptionScreen
import com.example.muscleiq.ui.screens.ProfileScreen
import com.example.muscleiq.ui.screens.WorkoutScreen
import com.example.muscleiq.ui.screens.MuscleMapScreen
import com.example.muscleiq.ui.screens.EditProfileScreen
import com.example.muscleiq.ui.screens.ChangePasswordScreen
import com.example.muscleiq.ui.screens.HelpCenterScreen
import com.example.muscleiq.ui.screens.RateAppScreen
import com.example.muscleiq.ui.theme.MuscleIQTheme
import com.example.muscleiq.ui.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MuscleIQTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MuscleIQApp()
                }
            }
        }
    }
}

@Composable
fun MuscleIQApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    
    // Check if user is logged in
    val startDestination = if (authViewModel.isUserLoggedIn()) "dashboard" else "auth_method"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("auth_method") {
            AuthMethodScreen(
                onNavigateToDashboard = {
                    navController.navigate("dashboard") {
                        popUpTo("auth_method") { inclusive = true }
                    }
                },
                onNavigateToEmailAuth = { navController.navigate("auth") },
                onNavigateToPhoneAuth = { navController.navigate("phone_auth") },
                viewModel = authViewModel
            )
        }
        
        composable("phone_auth") {
            PhoneAuthScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDashboard = {
                    navController.navigate("dashboard") {
                        popUpTo("auth_method") { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }

        composable("auth") {
            AuthScreen(
                onNavigateToDashboard = {
                    navController.navigate("dashboard") {
                        popUpTo("auth") { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }
        
        composable("dashboard") {
            DashboardScreen(
                onNavigateToWorkout = { navController.navigate("workout") },
                onNavigateToHistory = { navController.navigate("history") },
                onNavigateToMuscleMap = { navController.navigate("muscle_map") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToAiWorkout = { navController.navigate("ai_workout") },
                onNavigateToAnalytics = { navController.navigate("analytics") }
            )
        }
        
        composable("workout") {
            WorkoutScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable("history") {
            HistoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("muscle_map") {
            MuscleMapScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("profile") {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToNotifications = { navController.navigate("notifications") },
                onNavigateToAccountSettings = { navController.navigate("account_settings") },
                onNavigateToSubscription = { navController.navigate("subscription") },
                onNavigateToAuth = { 
                    navController.navigate("auth_method") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                }
            )
        }

        composable("notifications") {
            NotificationsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("account_settings") {
            AccountSettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEditProfile = { navController.navigate("edit_profile") },
                onNavigateToChangePassword = { navController.navigate("change_password") },
                onNavigateToSubscription = { navController.navigate("subscription") },
                onNavigateToHelpCenter = { navController.navigate("help_center") },
                onNavigateToRateApp = { navController.navigate("rate_app") },
                onNavigateToAuth = { 
                    navController.navigate("auth_method") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                }
            )
        }

        composable("subscription") {
            SubscriptionScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("edit_profile") {
            EditProfileScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("change_password") {
            ChangePasswordScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("help_center") {
            HelpCenterScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("rate_app") {
            RateAppScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("ai_workout") {
            com.example.muscleiq.ui.screens.AiGeneratorScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("analytics") {
            com.example.muscleiq.ui.screens.AnalyticsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
