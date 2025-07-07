package com.jmr.mediapowerhouse.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A custom switch component for toggling settings.
 * Mimics the iOS-style toggle switch.
 *
 * @param checked Current checked state of the switch.
 * @param onCheckedChange Callback to be invoked when the switch state changes.
 * @param modifier Modifier to be applied to the switch.
 * @param width Width of the switch track.
 * @param height Height of the switch track.
 * @param thumbSize Size of the thumb (circle).
 * @param checkedColor Color of the track when checked.
 * @param uncheckedColor Color of the track when unchecked.
 * @param thumbColor Color of the thumb.
 */
@Composable
fun CustomSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 44.dp,
    height: Dp = 24.dp,
    thumbSize: Dp = 16.dp,
    checkedColor: Color = MaterialTheme.colorScheme.primary,
    uncheckedColor: Color = Color(0xFFE0E0E0), // Light gray for unchecked
    thumbColor: Color = Color.White
) {
    val animatedTrackColor by animateColorAsState(
        targetValue = if (checked) checkedColor else uncheckedColor,
        animationSpec = tween(durationMillis = 200), label = "trackColorAnimation"
    )

    val animatedThumbOffset by animateFloatAsState(
        targetValue = if (checked) (width - thumbSize - 4.dp).toPx() else 4.dp.toPx(),
        animationSpec = tween(durationMillis = 200), label = "thumbOffsetAnimation"
    )

    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(height / 2))
            .background(animatedTrackColor)
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = (animatedThumbOffset / 1.dp).dp) // Convert px to dp for offset
                .size(thumbSize)
                .clip(CircleShape)
                .background(thumbColor)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CustomSwitchPreview() {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            Text("Dark Mode", modifier = Modifier.weight(1f))
            CustomSwitch(checked = true, onCheckedChange = {})
        }
        Spacer(Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            Text("Glassmorphism", modifier = Modifier.weight(1f))
            CustomSwitch(checked = false, onCheckedChange = {})
        }
    }
}
