package com.example.ui.theme

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

private val LightColorScheme = lightColorScheme(
    primary = AgriGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = AgriGreenContainer,
    onPrimaryContainer = AgriGreenDark,
    secondary = AgriGreenLight,
    onSecondary = Color.White,
    secondaryContainer = AgriGreenSurface,
    onSecondaryContainer = AgriGreenDark,
    tertiary = AgriGoldHarvest,
    onTertiary = Color.White,
    background = AgriBackground,
    onBackground = AgriTextPrimary,
    surface = AgriSurface,
    onSurface = AgriTextPrimary,
    surfaceVariant = AgriGreenSurface,
    onSurfaceVariant = AgriTextSecondary,
    outline = AgriBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = AgriGreenAccent,
    onPrimary = Color(0xFF00390B),
    primaryContainer = AgriGreenDark,
    onPrimaryContainer = AgriGreenContainer,
    secondary = AgriGreenLight,
    onSecondary = Color.White,
    background = Color(0xFF101912),
    onBackground = Color(0xFFE2E9E2),
    surface = Color(0xFF162218),
    onSurface = Color(0xFFE2E9E2),
    surfaceVariant = Color(0xFF1E2E21),
    onSurfaceVariant = Color(0xFFBAC7BB),
    outline = Color(0xFF334A37)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent agricultural branding
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
