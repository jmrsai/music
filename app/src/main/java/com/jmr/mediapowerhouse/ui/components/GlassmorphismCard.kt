package com.jmr.mediapowerhouse.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jmr.mediapowerhouse.ui.theme.GlassDarkBorder
import com.jmr.mediapowerhouse.ui.theme.GlassDarkSurface
import com.jmr.mediapowerhouse.ui.theme.GlassLightBorder
import com.jmr.mediapowerhouse.ui.theme.GlassLightSurface
import com.jmr.mediapowerhouse.viewmodel.ThemeViewModel

/**
 * A Composable that applies a glassmorphism effect.
 * It uses a translucent background and a subtle border to create the frosted glass look.
 *
 * @param modifier The modifier to be applied to the card.
 * @param themeViewModel The ViewModel to determine the current theme (light/dark).
 * @param content The composable content to be displayed inside the card.
 */
@Composable
fun GlassmorphismCard(
    modifier: Modifier = Modifier,
    themeViewModel: ThemeViewModel = viewModel(), // Inject ThemeViewModel
    content: @Composable ColumnScope.() -> Unit
) {
    val isDarkMode = themeViewModel.isDarkMode.value // Observe the theme state

    val backgroundColor = if (isDarkMode) GlassDarkSurface else GlassLightSurface
    val borderColor = if (isDarkMode) GlassDarkBorder else GlassLightBorder

    // Apply blur effect (requires RenderEffect on Android 12+)
    // For simplicity, this example focuses on color and border.
    // Real blur would involve a custom Modifier or RenderEffect.

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp)) // Rounded corners for the glass effect
            .background(backgroundColor) // Translucent background
            .border(
                width = 1.dp,
                color = borderColor, // Subtle border
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp), // Padding inside the card
            content = content
        )
    }
}
