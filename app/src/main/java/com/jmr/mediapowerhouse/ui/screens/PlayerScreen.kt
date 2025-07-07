package com.jmr.mediapowerhouse.ui.screens

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.session.MediaController
import androidx.media3.ui.PlayerView
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors

/**
 * Composable screen for media playback using ExoPlayer.
 * It integrates an Android `PlayerView` to display video and handle playback controls.
 *
 * @param mediaControllerFuture A ListenableFuture that will provide the MediaController instance.
 */
@Composable
fun PlayerScreen(
    mediaControllerFuture: ListenableFuture<MediaController>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val playerView = remember {
        PlayerView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            useController = true // Enable default playback controls
        }
    }

    DisposableEffect(mediaControllerFuture) {
        val listener = { controller: MediaController ->
            playerView.player = controller // Set the ExoPlayer instance to the PlayerView
        }
        mediaControllerFuture.addListener(listener, MoreExecutors.directExecutor())

        onDispose {
            // Release the player from the view when the composable is disposed
            playerView.player?.release()
            playerView.player = null
            // It's important to release the future if it's no longer needed
            MediaController.releaseFuture(mediaControllerFuture)
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { playerView },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Take up available vertical space
        )
        // You can add custom controls or information below the player view if needed
    }
}
