package com.example.muscleiq

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navDeepLink
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Scaffold
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.muscleiq.ui.screens.AuthScreen
import com.example.muscleiq.ui.screens.AuthMethodScreen
import com.example.muscleiq.ui.screens.PhoneAuthScreen
import com.example.muscleiq.ui.screens.EmailMagicLinkScreen
import com.example.muscleiq.ui.screens.DashboardScreen
import com.example.muscleiq.ui.screens.HistoryScreen
import com.example.muscleiq.ui.screens.NotificationsScreen
import com.example.muscleiq.ui.screens.AccountSettingsScreen
import com.example.muscleiq.ui.screens.SubscriptionScreen
import com.example.muscleiq.ui.screens.ProfileScreen
import com.example.muscleiq.ui.screens.RegisterScreen
import com.example.muscleiq.ui.screens.WorkoutScreen
import com.example.muscleiq.ui.screens.MuscleMapScreen
import com.example.muscleiq.ui.screens.EditProfileScreen
import com.example.muscleiq.ui.screens.ChangePasswordScreen
import com.example.muscleiq.ui.screens.HelpCenterScreen
import com.example.muscleiq.ui.screens.RateAppScreen
import com.example.muscleiq.ui.theme.MuscleIQTheme
import com.example.muscleiq.ui.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Log out on fresh app start (e.g., when removed from recents)
        if (savedInstanceState == null) {
            FirebaseAuth.getInstance().signOut()
        }
        
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

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

@Composable
fun MuscleIQApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    
    // Check if user is logged in
    val startDestination = if (authViewModel.isUserLoggedIn()) "dashboard" else "auth_method"

    val items = listOf(
        BottomNavItem("dashboard", "Home", Icons.Default.Home),
        BottomNavItem("analytics", "Analytics", Icons.Default.List),
        BottomNavItem("ai_workout", "AI Coach", Icons.Default.Star),
        BottomNavItem("profile", "Profile", Icons.Default.Person)
    )
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val showBottomBar = currentRoute in items.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = com.example.muscleiq.ui.theme.Dark200,
                    contentColor = com.example.muscleiq.ui.theme.Accent
                ) {
                    items.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = currentRoute == item.route,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = com.example.muscleiq.ui.theme.Accent,
                                selectedTextColor = com.example.muscleiq.ui.theme.Accent,
                                indicatorColor = com.example.muscleiq.ui.theme.Dark300,
                                unselectedIconColor = androidx.compose.ui.graphics.Color.Gray,
                                unselectedTextColor = androidx.compose.ui.graphics.Color.Gray
                            ),
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController, 
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
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
        
        composable(
            "email_magic_link"
        ) {
            EmailMagicLinkScreen(
                viewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDashboard = {
                    navController.navigate("dashboard") {
                        popUpTo("auth_method") { inclusive = true }
                    }
                }
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
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                viewModel = authViewModel
            )
        }
        
        composable("register") {
            RegisterScreen(
                onNavigateToDashboard = {
                    navController.navigate("dashboard") {
                        popUpTo("auth_method") { inclusive = true } // Clear backstack
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack() // Go back to AuthScreen
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
                onNavigateToAiDiet = { navController.navigate("ai_diet") },
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
        
        composable("ai_diet") {
            com.example.muscleiq.ui.screens.AiDietScreen(
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
}
