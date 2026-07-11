package com.example.sologym.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    background = DarkBlue,
    surface = DarkBlue,
    primary = LightBlue,
    secondary = LightBlue,
    tertiary = FullBlack,

    onSurface = FullWhite,
    onBackground = FullWhite,

    primaryContainer = LightBlue,
    onPrimary = FullWhite,

    secondaryContainer = LightBlue,
    onSecondary = FullWhite,

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun SoloGymTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}