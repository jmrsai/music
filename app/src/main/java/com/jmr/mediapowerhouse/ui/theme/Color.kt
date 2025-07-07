package com.jmr.mediapowerhouse.ui.theme

import androidx.compose.ui.graphics.Color

// Light Theme Colors
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

// Dark Theme Colors
val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Custom Colors for Glassmorphism and UI elements
// These are examples and can be fine-tuned for your specific aesthetic.
val LightBackground = Color(0xFFF0F2F5) // A very light grey for backgrounds
val DarkBackground = Color(0xFF121212) // A deep dark grey for backgrounds

val LightSurface = Color(0xFFFFFFFF) // White surface for light theme
val DarkSurface = Color(0xFF1E1E1E) // Darker surface for dark theme

val LightPrimary = Color(0xFF6200EE) // A vibrant primary color for light theme
val DarkPrimary = Color(0xFFBB86FC) // A lighter primary color for dark theme

val LightOnPrimary = Color.White
val DarkOnPrimary = Color.Black

val LightSecondary = Color(0xFF03DAC6) // A complementary secondary color for light theme
val DarkSecondary = Color(0xFF03DAC6) // Same for dark theme, can be adjusted

val LightOnSecondary = Color.Black
val DarkOnSecondary = Color.Black

val LightTertiary = Color(0xFF3700B3) // Another accent color for light theme
val DarkTertiary = Color(0xFF018786) // Another accent color for dark theme

val LightOnSurface = Color(0xFF1C1B1F) // Text on light surfaces
val DarkOnSurface = Color(0xFFE6E1E5) // Text on dark surfaces

// Glassmorphism specific colors (adjusted for transparency and blur effect)
// These colors are used within the GlassmorphismCard for its background and border.
// The alpha value (e.g., 0x40 for 25% opacity) is key for the translucent effect.
val GlassLightSurface = Color(0x40FFFFFF) // White with 25% opacity for light glass
val GlassDarkSurface = Color(0x20000000) // Black with 12.5% opacity for dark glass (subtle)
val GlassLightBorder = Color(0x80FFFFFF) // White with 50% opacity for light glass border
val GlassDarkBorder =
    Color(0x40FFFFFF) // White with 25% opacity for dark glass border (more visible)
