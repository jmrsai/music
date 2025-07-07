package com.jmr.mediapowerhouse.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController

/**
 * ViewModel responsible for managing media playback state and interacting with the MediaController.
 * It provides functions to set the MediaController and play media items.
 */
class PlayerViewModel : ViewModel() {

    private var mediaController: MediaController? = null

    /**
     * Sets the MediaController instance for the ViewModel to interact with.
     * @param controller The MediaController instance.
     */
    fun setMediaController(controller: MediaController) {
        mediaController = controller
    }

    /**
     * Plays a list of media items starting from a specific index.
     * @param mediaItems The list of media URIs to play.
     * @param startIndex The index of the media item to start playback from.
     */
    fun playMedia(mediaItems: List<Uri>, startIndex: Int) {
        mediaController?.let { controller ->
            val mediaItemsList = mediaItems.map { MediaItem.fromUri(it) }
            controller.setMediaItems(
                mediaItemsList,
                startIndex,
                0L
            ) // Start from beginning of selected item
            controller.prepare()
            controller.play()
        }
    }

    /**
     * Pauses the currently playing media.
     */
    fun pauseMedia() {
        mediaController?.pause()
    }

    /**
     * Resumes the currently paused media.
     */
    fun resumeMedia() {
        mediaController?.play()
    }

    /**
     * Seeks to a specific position in the current media.
     * @param positionMs The position in milliseconds to seek to.
     */
    fun seekTo(positionMs: Long) {
        mediaController?.seekTo(positionMs)
    }

    /**
     * Releases the MediaController when the ViewModel is no longer needed.
     */
    override fun onCleared() {
        super.onCleared()
        mediaController?.release()
        mediaController = null
    }
}
