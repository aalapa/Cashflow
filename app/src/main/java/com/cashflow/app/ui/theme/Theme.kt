package com.cashflow.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// myBudgy - Modern Purple & Teal Theme
private val DarkColorScheme = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF9D4EDD), // Vibrant purple
    secondary = androidx.compose.ui.graphics.Color(0xFF06B6D4), // Cyan/Teal
    tertiary = androidx.compose.ui.graphics.Color(0xFFFF6B9D), // Pink accent
    background = androidx.compose.ui.graphics.Color(0xFF0F0B1E), // Deep purple-black
    surface = androidx.compose.ui.graphics.Color(0xFF1A1625),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFF2A2535),
    onPrimary = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    onSecondary = androidx.compose.ui.graphics.Color(0xFF000000),
    onBackground = androidx.compose.ui.graphics.Color(0xFFE8E4F0),
    onSurface = androidx.compose.ui.graphics.Color(0xFFE8E4F0),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFFB8B4C0),
    error = androidx.compose.ui.graphics.Color(0xFFFF5252)
)

private val LightColorScheme = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF7C3AED), // Rich purple
    secondary = androidx.compose.ui.graphics.Color(0xFF0891B2), // Teal
    tertiary = androidx.compose.ui.graphics.Color(0xFFEC4899), // Pink
    background = androidx.compose.ui.graphics.Color(0xFFFAF5FF), // Light purple tint
    surface = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFFF3E8FF), // Very light purple
    onPrimary = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    onSecondary = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    onBackground = androidx.compose.ui.graphics.Color(0xFF1E1B24),
    onSurface = androidx.compose.ui.graphics.Color(0xFF1E1B24),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF4B5563),
    error = androidx.compose.ui.graphics.Color(0xFFDC2626)
)

@Composable
fun CashFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
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
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

      MaterialTheme(
          colorScheme = colorScheme,
          typography = CompactTypography,
          content = content
      )
}

