package com.kotlinjsonui.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE),
    onPrimary = Color.White,
    secondary = Color(0xFF03DAC6),
    onSecondary = Color.Black,
    background = Color(0xFFF5F5F5),
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC),
    onPrimary = Color.Black,
    secondary = Color(0xFF03DAC6),
    onSecondary = Color.Black,
    background = Color(0xFF121212),
    onBackground = Color.White,
    surface = Color(0xFF121212),
    onSurface = Color.White,
)

@Composable
fun KotlinJsonUITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
