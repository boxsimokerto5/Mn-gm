package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = SunGoldDark,
    onPrimary = CardSurface,
    primaryContainer = SunGoldLight,
    onPrimaryContainer = TextPrimary,

    secondary = OceanCyan,
    onSecondary = CardSurface,
    secondaryContainer = OceanCyanLight,
    onSecondaryContainer = TextPrimary,

    tertiary = CoralCandy,
    onTertiary = CardSurface,
    tertiaryContainer = CoralCandyLight,
    onTertiaryContainer = TextPrimary,

    background = CreamBackground,
    onBackground = TextPrimary,
    surface = CardSurface,
    onSurface = TextPrimary,
    surfaceVariant = SunGoldLight.copy(alpha = 0.35f),
    onSurfaceVariant = TextSecondary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Always use our cheerful bright palette for the kid-friendly experience
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = CreamBackground.toArgb()
                window.navigationBarColor = CardSurface.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = true
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
