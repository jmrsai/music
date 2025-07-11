package com.jmr.mediapowerhouse.data

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize // Correct import for @Parcelize

@Parcelize // Apply the Parcelize annotation
data class `MediaItem.kt`(
    val id: Long,
    val title: String,
    val artist: String?,
    val uri: Uri,
    val isVideo: Boolean
) : Parcelable