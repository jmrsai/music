package com.jmr.mediapowerhouse.viewmodel

import android.app.Application
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel responsible for loading and managing media (audio and video) from the device.
 * It provides filtered lists based on a search query.
 */
class MediaViewModel(application: Application) : AndroidViewModel(application) {

    private val _audioList = MutableStateFlow<List<Uri>>(emptyList())
    private val _videoList = MutableStateFlow<List<Uri>>(emptyList())

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val searchQuery = MutableStateFlow("")

    // Combined flow for filtered audio list
    val filteredAudioList: StateFlow<List<Uri>> =
        _audioList.combine(searchQuery) { audioList, query ->
            if (query.isBlank()) {
                audioList
            } else {
                audioList.filter { uri ->
                    uri.lastPathSegment?.contains(query, ignoreCase = true) == true
                }
            }
        }.asStateFlow()

    // Combined flow for filtered video list
    val filteredVideoList: StateFlow<List<Uri>> =
        _videoList.combine(searchQuery) { videoList, query ->
            if (query.isBlank()) {
                videoList
            } else {
                videoList.filter { uri ->
                    uri.lastPathSegment?.contains(query, ignoreCase = true) == true
                }
            }
        }.asStateFlow()

    init {
        loadMedia() // Load media when the ViewModel is initialized
    }

    /**
     * Loads audio and video media files from the device's external storage.
     * This operation is performed on a background thread (IO dispatcher).
     */
    private fun loadMedia() {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val currentAudioList = mutableListOf<Uri>()
            val currentVideoList = mutableListOf<Uri>()

            // Query for audio files
            val audioCollection =
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
                } else {
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }

            val audioProjection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE
            )

            getApplication<Application>().contentResolver.query(
                audioCollection,
                audioProjection,
                null,
                null,
                "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    cursor.getInt(durationColumn)
                    cursor.getInt(sizeColumn)
                    val contentUri: Uri =
                        ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                    currentAudioList.add(contentUri)
                    Log.d("MediaViewModel", "Found audio: $name, Uri: $contentUri")
                }
            }

            // Query for video files
            val videoCollection =
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
                } else {
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                }

            val videoProjection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE
            )

            getApplication<Application>().contentResolver.query(
                videoCollection,
                videoProjection,
                null,
                null,
                "${MediaStore.Video.Media.DISPLAY_NAME} ASC"
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    cursor.getInt(durationColumn)
                    cursor.getInt(sizeColumn)
                    val contentUri: Uri =
                        ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
                    currentVideoList.add(contentUri)
                    Log.d("MediaViewModel", "Found video: $name, Uri: $contentUri")
                }
            }

            withContext(Dispatchers.Main) {
                _audioList.value = currentAudioList
                _videoList.value = currentVideoList
                _isLoading.value = false
            }
        }
    }
}
