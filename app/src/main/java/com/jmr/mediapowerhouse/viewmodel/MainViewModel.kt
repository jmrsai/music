package com.jmr.mediapowerhouse.viewmodel

import android.app.Application
import android.content.ContentUris
import android.content.Context
import android.net.Uri // Import Uri
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jmr.mediapowerhouse.data.AppMediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Define an enum for sorting options
enum class MediaSortOrder {
    TITLE_ASC,
    ARTIST_ASC
}

class MainViewModel(private val application: Application) : ViewModel() {

    private val _audioList = MutableStateFlow<List<AppMediaItem>>(emptyList())
    private val _videoList = MutableStateFlow<List<AppMediaItem>>(emptyList())
    val searchQuery = MutableStateFlow("")
    val isLoading = MutableStateFlow(true)
    val isMediaLoaded = MutableStateFlow(false)

    val currentPlayingMediaItem = MutableStateFlow<AppMediaItem?>(null)

    val sortOrder = MutableStateFlow(MediaSortOrder.TITLE_ASC)

    // New StateFlow to hold the user-selected download directory URI
    val downloadDirectoryUri = MutableStateFlow<Uri?>(null)

    val filteredAudioList: StateFlow<List<AppMediaItem>> =
        combine(_audioList, searchQuery, sortOrder) { audioList, query, currentSortOrder ->
            val filtered = if (query.isBlank()) {
                audioList
            } else {
                audioList.filter {
                    it.title.contains(query, ignoreCase = true) ||
                            it.artist?.contains(query, ignoreCase = true) == true ||
                            it.album?.contains(query, ignoreCase = true) == true
                }
            }
            when (currentSortOrder) {
                MediaSortOrder.TITLE_ASC -> filtered.sortedBy { it.title }
                MediaSortOrder.ARTIST_ASC -> filtered.sortedBy { it.artist ?: "" }
            }
        }.asStateFlow()

    val filteredVideoList: StateFlow<List<AppMediaItem>> =
        combine(_videoList, searchQuery, sortOrder) { videoList, query, currentSortOrder ->
            val filtered = if (query.isBlank()) {
                videoList
            } else {
                videoList.filter {
                    it.title.contains(query, ignoreCase = true) ||
                            it.artist?.contains(query, ignoreCase = true) == true ||
                            it.album?.contains(query, ignoreCase = true) == true
                }
            }
            when (currentSortOrder) {
                MediaSortOrder.TITLE_ASC -> filtered.sortedBy { it.title }
                MediaSortOrder.ARTIST_ASC -> filtered.sortedBy { it.artist ?: "" }
            }
        }.asStateFlow()

    fun loadMedia() {
        if (isMediaLoaded.value) return
        isLoading.value = true
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _audioList.value = getAudioFiles(application)
                _videoList.value = getVideoFiles(application)
                isLoading.value = false
            }
        }
    }

    fun setCurrentPlayingMediaItem(item: AppMediaItem?) {
        currentPlayingMediaItem.value = item
    }

    fun setSortOrder(newSortOrder: MediaSortOrder) {
        sortOrder.value = newSortOrder
    }

    // Function to set the user-selected download directory URI
    fun setDownloadDirectoryUri(uri: Uri?) {
        downloadDirectoryUri.value = uri
    }

    private fun getAudioFiles(context: Context): List<AppMediaItem> {
        val audioList = mutableListOf<AppMediaItem>()
        val collection = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
        )

        val sortOrder = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"

        context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val artist = cursor.getString(artistColumn)
                val album = cursor.getString(albumColumn)
                val duration = cursor.getLong(durationColumn)
                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                audioList.add(AppMediaItem(id, contentUri, name, artist, album, duration, true))
            }
        }
        return audioList
    }

    private fun getVideoFiles(context: Context): List<AppMediaItem> {
        val videoList = mutableListOf<AppMediaItem>()
        val collection = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.ARTIST,
            MediaStore.Video.Media.ALBUM,
            MediaStore.Video.Media.DURATION,
        )

        val sortOrder = "${MediaStore.Video.Media.DISPLAY_NAME} ASC"

        context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val artist = cursor.getString(artistColumn)
                val album = cursor.getString(albumColumn)
                val duration = cursor.getLong(durationColumn)
                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                videoList.add(AppMediaItem(id, contentUri, name, artist, album, duration, false))
            }
        }
        return videoList
    }
}