package com.jmr.mediapowerhouse.data

import android.net.Uri
import androidx.media3.common.MediaItem

data class AppMediaItem(
    val id: Long,
    val uri: Uri,
    val title: String,
    val artist: String?,
    val album: String?,
    val duration: Long, // in milliseconds
    val isAudio: Boolean
)

// Extension function to convert AppMediaItem to MediaItem
fun AppMediaItem.toMediaItem(): MediaItem {
    return MediaItem.Builder()
        .setMediaId(id.toString())
        .setUri(uri)
        .setMediaMetadata(
            androidx.media3.common.MediaMetadata.Builder()
                .setTitle(title)
                .setArtist(artist)
                .setAlbumTitle(album)
                .setIsPlayable(true)
                .build()
        )
        .build()
}