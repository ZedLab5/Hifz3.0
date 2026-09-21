package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryTealLight,
    onPrimary = Color.White,
    primaryContainer = GoldTintBgLight,
    onPrimaryContainer = TextPrimaryLight,
    secondary = SecondaryGoldLight,
    onSecondary = Color.White,
    secondaryContainer = GoldTintBgLight,
    onSecondaryContainer = TextPrimaryLight,
    tertiary = PrimaryTealLight,
    onTertiary = Color.White,
    background = CanvasMint,
    onBackground = TextPrimaryLight,
    surface = SurfaceWhite,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceElevatedLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = BorderDividerLight,
    outlineVariant = BorderDividerLight,
    error = DangerRedLight,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryTealDark,
    onPrimary = SurfaceDark,
    primaryContainer = GoldTintBgDark,
    onPrimaryContainer = TextPrimaryDark,
    secondary = SecondaryGoldDark,
    onSecondary = SurfaceDark,
    secondaryContainer = GoldTintBgDark,
    onSecondaryContainer = TextPrimaryDark,
    tertiary = PrimaryTealDark,
    onTertiary = Color.White,
    background = CanvasDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceElevatedDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = BorderDividerDark,
    outlineVariant = BorderDividerDark,
    error = DangerRedDark,
    onError = Color.White
)

@Composable
fun NoorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
