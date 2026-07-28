package com.example.muscleiq.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Custom colors from Tailwind config
val DarkBackground = Color(0xFF0A0A0F)
val Dark100 = Color(0xFF13131A)
val Dark200 = Color(0xFF1C1C26)
val Dark300 = Color(0xFF2A2A35)
val Dark400 = Color(0xFF3F3F4E)
val Accent = Color(0xFF3B82F6)
val AccentHover = Color(0xFF2563EB)
val Warning = Color(0xFFF59E0B)

val MuscleGreen = Color(0xFF10B981)
val MuscleYellow = Color(0xFFF59E0B)
val MuscleRed = Color(0xFFEF4444)
val MuscleOvertrained = Color(0xFF8B5CF6)

private val AppColorScheme = darkColorScheme(
    primary = Accent,
    onPrimary = Color.White,
    secondary = Dark300,
    onSecondary = Color.White,
    background = DarkBackground,
    onBackground = Color.White,
    surface = Dark200,
    onSurface = Color.White,
    surfaceVariant = Dark100,
    onSurfaceVariant = Color(0xFF9CA3AF), // Gray-400 equivalent
    error = MuscleRed,
    onError = Color.White
)

@Composable
fun MuscleIQTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = AppColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
