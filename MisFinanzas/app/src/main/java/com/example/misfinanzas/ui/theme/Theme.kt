package com.example.misfinanzas.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF10B981),
    secondary = Color(0xFF61CBB3),
    tertiary = Color(0xFFEDE9D0),
    background = Color(0xFF192837),
    surface = Color(0xFF192837),
    onBackground = Color(0xFFEDE9D0),
    onSurface = Color.Black,
    onPrimary = Color(0xFF192837),
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF10B981),
    secondary = Color(0xFF61CBB3),
    tertiary = Color(0xFFEDE9D0),
    background = Color(0xFF192837),
    surface = Color(0xFF192837),
    onBackground = Color(0xFFEDE9D0),
    onSurface = Color.Black,
    onPrimary = Color(0xFF192837),
)

@Composable
fun MisFinanzasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}