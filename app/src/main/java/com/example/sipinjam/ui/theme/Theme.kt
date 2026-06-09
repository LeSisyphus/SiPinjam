package com.example.sipinjam.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val LightColorScheme = lightColorScheme(
    primary = LightAppColors.primary,
    onPrimary = LightAppColors.onPrimary,
    background = LightAppColors.background,
    onBackground = LightAppColors.textPrimary,
    surface = LightAppColors.card,
    onSurface = LightAppColors.textPrimary,
    surfaceVariant = LightAppColors.inputBackground,
    onSurfaceVariant = LightAppColors.textSecondary,
    error = LightAppColors.statusRed,
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkAppColors.primary,
    onPrimary = DarkAppColors.onPrimary,
    background = DarkAppColors.background,
    onBackground = DarkAppColors.textPrimary,
    surface = DarkAppColors.card,
    onSurface = DarkAppColors.textPrimary,
    surfaceVariant = DarkAppColors.inputBackground,
    onSurfaceVariant = DarkAppColors.textSecondary,
    error = DarkAppColors.statusRed,
)

@Composable
fun SiPinjamTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val appColors = if (darkTheme) DarkAppColors else LightAppColors
    val materialColors = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colorScheme = materialColors,
            typography = Typography,
            content = content,
        )
    }
}
