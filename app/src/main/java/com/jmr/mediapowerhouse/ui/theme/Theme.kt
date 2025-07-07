package com.jmr.mediapowerhouse.ui.theme

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

// Define the light color scheme for Material 3
private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    tertiary = LightTertiary,
    onTertiary = LightOnPrimary, // Assuming onTertiary is similar to onPrimary for text contrast
    background = LightBackground,
    onBackground = LightOnSurface, // Text on background
    surface = LightSurface,
    onSurface = LightOnSurface, // Text on surface
    surfaceVariant = PurpleGrey80, // Used for less prominent surfaces
    onSurfaceVariant = DarkBackground // Text on surface variant
)

// Define the dark color scheme for Material 3
private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    tertiary = DarkTertiary,
    onTertiary = DarkOnPrimary, // Assuming onTertiary is similar to onPrimary for text contrast
    background = DarkBackground,
    onBackground = DarkOnSurface, // Text on background
    surface = DarkSurface,
    onSurface = DarkOnSurface, // Text on surface
    surfaceVariant = PurpleGrey40, // Used for less prominent surfaces
    onSurfaceVariant = LightBackground // Text on surface variant
)

/**
 * Main theme composable for Media Powerhouse.
 * It dynamically selects between light and dark themes, and supports dynamic theming on Android 12+.
 *
 * @param darkTheme If true, the dark theme is applied. Defaults to system setting.
 * @param dynamicColor If true, dynamic colors from wallpaper are used on Android 12+.
 * @param content The composable content to apply the theme to.
 */
@Composable
fun MediaPowerhouseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available only on Android 12+
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
            // Set status bar color based on the primary color of the selected theme
            window.statusBarColor = colorScheme.primary.toArgb()
            // Adjust system bar icons for light/dark content
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Assuming Typography is defined in Typography.kt
        content = content
    )
}
