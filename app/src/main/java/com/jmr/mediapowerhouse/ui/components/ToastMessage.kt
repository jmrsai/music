package com.jmr.mediapowerhouse.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * A simple, customizable toast message Composable.
 * It appears at the top of the screen and automatically disappears after a duration.
 *
 * @param message The text message to display in the toast.
 * @param durationMillis The duration (in milliseconds) for which the toast should be visible.
 * @param onDismiss Callback invoked when the toast is dismissed (either by timeout or manually).
 */
@Composable
fun ToastMessage(
    message: String,
    durationMillis: Long = 3000L,
    onDismiss: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(message) {
        visible = true
        delay(durationMillis)
        visible = false
        delay(300) // Wait for fade out animation
        onDismiss()
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .background(
                    Color(0xCC333333),
                    RoundedCornerShape(8.dp)
                ) // Dark translucent background
                .padding(12.dp)
                .align(Alignment.TopCenter)
        ) {
            Text(
                text = message,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
