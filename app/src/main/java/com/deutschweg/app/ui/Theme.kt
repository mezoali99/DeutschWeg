package com.deutschweg.app.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF8B1A1A),
    onPrimary = Color.White,
    secondary = Color(0xFF7A5C00),
    tertiary = Color(0xFF2E7D32)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFFFB4AB),
    onPrimary = Color(0xFF410002),
    secondary = Color(0xFFF9C74F),
    tertiary = Color(0xFF81C784)
)

@Composable
fun DeutschWegTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        content = content
    )
}
