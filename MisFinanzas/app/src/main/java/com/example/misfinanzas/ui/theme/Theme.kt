package com.example.misfinanzas.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFEDE9D0),
    secondary = Color(0xFF61CBB3),
    tertiary = Color(0xFFEDE9D0),
    background = Color(0xFF192837),
    surface = Color(0xFF192837),
    onBackground = Color(0xFFEDE9D0),
    onSurface = Color(0xFFEDE9D0),
    onPrimary = Color(0xFF192837)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF192837),
    secondary = Color(0xFF61CBB3),
    tertiary = Color(0xFFEDE9D0),
    background = Color(0xFF192837),
    surface = Color(0xFF192837),
    onBackground = Color(0xFFEDE9D0),
    onSurface = Color(0xFF192837),
    onPrimary = Color(0xFF192837)
)

@Composable
fun MisFinanzasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}