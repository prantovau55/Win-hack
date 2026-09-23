package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AiCyan,
    onPrimary = Color.Black,
    primaryContainer = SurfaceCardHigh,
    onPrimaryContainer = TextPrimary,
    secondary = SignalBig,
    onSecondary = Color.Black,
    secondaryContainer = SignalBigBg,
    onSecondaryContainer = SignalBig,
    tertiary = ColorViolet,
    background = BgDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = TextSecondary,
    outline = BorderDark,
    outlineVariant = BorderGlow
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
