package com.example.coffeeorder.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Brown40,
    onPrimary = Cream,
    primaryContainer = LightBrown80,
    onPrimaryContainer = DarkBrown,
    secondary = Brown60,
    onSecondary = Cream,
    secondaryContainer = LightBrown60,
    background = Cream,
    onBackground = DarkBrown,
    surface = Cream,
    onSurface = DarkBrown
)

private val DarkColorScheme = darkColorScheme(
    primary = Brown80,
    onPrimary = DarkBrown,
    primaryContainer = Brown40,
    onPrimaryContainer = Cream,
    secondary = LightBrown80,
    onSecondary = DarkBrown,
    secondaryContainer = Brown60,
    background = DarkBrown,
    onBackground = Cream,
    surface = DarkBrown,
    onSurface = Cream
)

@Composable
fun CoffeeOrderTheme(
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
