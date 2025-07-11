package com.jmr.mediapowerhouse.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

/**
 * A MediaSessionService that hosts a MediaSession and an ExoPlayer instance.
 * This service allows background media playback and integrates with system media controls.
 */
@OptIn(UnstableApi::class) // For @UnstableApi annotations from Media3
class `PlaybackService.kt` : MediaSessionService() {

    private var mediaSession: MediaSession? = null
    private var exoPlayer: ExoPlayer? = null

    companion object {
        private const val PLAYBACK_NOTIFICATION_CHANNEL_ID = "playback_channel"
        private const val PLAYBACK_NOTIFICATION_CHANNEL_NAME = "Media Playback"
    }

    /**
     * Called when the service is first created.
     * Initializes the ExoPlayer and MediaSession.
     */
    override fun onCreate() {
        super.onCreate()
        // Create a notification channel for the foreground service
        createNotificationChannel()

        // Configure audio attributes for the player
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

        // Initialize ExoPlayer
        exoPlayer = ExoPlayer.Builder(this)
            .setAudioAttributes(audioAttributes, true) // Handle audio focus automatically
            .build()

        // Build MediaSession
        mediaSession = MediaSession.Builder(this, exoPlayer!!)
            .build()
    }

    /**
     * Called when a client requests to bind to the service.
     * Returns the SessionToken for the MediaSession.
     */
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession

    /**
     * Called when the service is no longer used and is being destroyed.
     * Releases the MediaSession and ExoPlayer resources.
     */
    override fun onDestroy() {
        mediaSession?.run {
            player.release() // Release the player
            release() // Release the media session
            mediaSession = null
        }
        exoPlayer = null
        super.onDestroy()
    }

    /**
     * Creates a notification channel for Android O (API 26) and above.
     * This is required to display foreground service notifications for media playback.
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                PLAYBACK_NOTIFICATION_CHANNEL_ID,
                PLAYBACK_NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW // Low importance for less intrusive notifications
            )
            val notificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
