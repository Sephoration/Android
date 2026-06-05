package com.example.loginapp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Blue40,
    onPrimary = White,
    primaryContainer = LightBlue80,
    onPrimaryContainer = DarkBlue,
    secondary = Blue60,
    onSecondary = White,
    background = LightGray,
    onBackground = DarkBlue,
    surface = White,
    onSurface = DarkBlue,
    error = ErrorRed
)

private val DarkColorScheme = darkColorScheme(
    primary = Blue80,
    onPrimary = DarkBlue,
    primaryContainer = Blue40,
    onPrimaryContainer = LightBlue80,
    secondary = LightBlue80,
    onSecondary = DarkBlue,
    background = DarkBlue,
    onBackground = White,
    surface = DarkBlue,
    onSurface = White,
    error = ErrorRed
)

@Composable
fun LoginAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
