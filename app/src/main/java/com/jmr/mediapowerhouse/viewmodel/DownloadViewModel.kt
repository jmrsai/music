package com.jmr.mediapowerhouse.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

// Data class to represent a download item
data class DownloadItem(
    val id: Long,
    val name: String,
    val type: String,
    var status: String,
    var progress: Float, // 0.0 to 100.0
    val size: Long // in bytes
)

/**
 * ViewModel for managing all simulated download items across the application.
 * It handles adding, removing, and updating the progress of downloads.
 */
class DownloadViewModel : ViewModel() {

    private val _downloads = MutableStateFlow<List<DownloadItem>>(emptyList())
    val downloads: StateFlow<List<DownloadItem>> = _downloads.asStateFlow()

    private val _isHistoryEnabled = MutableStateFlow(true)
    val isHistoryEnabled: StateFlow<Boolean> = _isHistoryEnabled.asStateFlow()

    init {
        // Start simulating progress for existing downloads (if any, e.g., restored from state)
        // This will continuously run in the background as long as the ViewModel is alive.
        viewModelScope.launch {
            while (true) {
                _downloads.update { currentDownloads ->
                    currentDownloads.map { download ->
                        if (download.progress < 100f) {
                            // Simulate incremental progress
                            val newProgress =
                                (download.progress + Random.nextFloat() * 5).coerceAtMost(100f)
                            download.copy(
                                progress = newProgress,
                                status = if (newProgress >= 100f) "Completed" else "Downloading"
                            )
                        } else {
                            download // Already completed
                        }
                    }
                }
                delay(1000) // Update every second
            }
        }
    }

    /**
     * Adds a new simulated download item to the list.
     * @param name The name of the download.
     * @param type The type of content (e.g., "Video", "Audio", "Torrent").
     * @param size The total size of the download in bytes.
     */
    fun addDownload(name: String, type: String, size: Long) {
        val newDownload = DownloadItem(
            id = System.currentTimeMillis(),
            name = name,
            type = type,
            status = "Queued",
            progress = 0f,
            size = size
        )
        _downloads.update { currentList ->
            currentList + newDownload
        }
    }

    /**
     * Removes a download item from the list by its ID.
     * @param id The ID of the download item to remove.
     */
    fun removeDownload(id: Long) {
        _downloads.update { currentList ->
            currentList.filter { it.id != id }
        }
    }

    /**
     * Clears all download items from the history.
     */
    fun clearAllDownloads() {
        _downloads.update { emptyList() }
    }

    /**
     * Toggles the state of download history.
     * (Note: For a real app, this would control persistence to a database/preferences).
     */
    fun toggleHistoryEnabled() {
        _isHistoryEnabled.update { !it }
    }
}
