package com.jmr.mediapowerhouse.viewmodel

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jmr.mediapowerhouse.service.`TorrentDownloadService.kt`
import com.jmr.mediapowerhouse.service.`TorrentDownloadService.kt`.TorrentDownloadServiceBinder
import com.jmr.mediapowerhouse.service.TorrentInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Torrent Downloader feature.
 * It manages the UI state related to torrent downloads and interacts with the
 * TorrentDownloadService to initiate and monitor downloads.
 */
class `TorrentViewModel.kt` : ViewModel() {

    private val TAG = "TorrentViewModel"

    // State for the magnet URI input field
    private val _magnetUriInput = mutableStateOf("")
    val magnetUriInput: State<String> = _magnetUriInput

    // State to hold the list of active torrents
    private val _activeTorrents = MutableStateFlow<Map<String, TorrentInfo>>(emptyMap())
    val activeTorrents: StateFlow<Map<String, TorrentInfo>> = _activeTorrents.asStateFlow()

    // State to track if the service is bound
    private val _isServiceBound = mutableStateOf(false)
    val isServiceBound: State<Boolean> = _isServiceBound

    private var torrentService: TorrentDownloadService? = null

    // ServiceConnection to monitor the state of the TorrentDownloadService
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(TAG, "TorrentDownloadService connected")
            val binder = service as TorrentDownloadServiceBinder
            torrentService = binder.service
            _isServiceBound.value = true

            // Observe the activeTorrents flow from the service
            viewModelScope.launch {
                torrentService?.activeTorrents?.collect { torrentsMap ->
                    _activeTorrents.value =
                        torrentsMap // Update ViewModel's state with service data
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "TorrentDownloadService disconnected")
            torrentService = null
            _isServiceBound.value = false
            _activeTorrents.value = emptyMap() // Clear torrents if service disconnects
        }
    }

    /**
     * Updates the magnet URI input field.
     */
    fun onMagnetUriInputChange(newValue: String) {
        _magnetUriInput.value = newValue
    }

    /**
     * Starts the TorrentDownloadService and binds to it.
     * This should be called when the TorrentScreen is first composed or becomes active.
     */
    fun startAndBindService(context: Context) {
        if (!isServiceBound.value) {
            Log.d(TAG, "Starting and binding TorrentDownloadService")
            val serviceIntent = Intent(context, TorrentDownloadService::class.java)
            // Start the service (important for foreground service lifecycle)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
            // Bind to the service
            context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    /**
     * Unbinds from the TorrentDownloadService.
     * This should be called when the TorrentScreen is no longer needed.
     */
    fun unbindService(context: Context) {
        if (isServiceBound.value) {
            Log.d(TAG, "Unbinding from TorrentDownloadService")
            context.unbindService(serviceConnection)
            _isServiceBound.value = false
            torrentService = null
            _activeTorrents.value = emptyMap()
        }
    }

    /**
     * Initiates a torrent download using the provided magnet URI.
     *
     * @param magnetUri The magnet link to download.
     * @param downloadPath The path where the torrent content will be saved.
     */
    fun downloadTorrent(context: Context, magnetUri: String, downloadPath: String) {
        if (magnetUri.isBlank()) {
            Log.w(TAG, "Cannot download: Magnet URI is empty.")
            // Optionally, show a user-friendly message (e.g., Toast or Snackbar)
            return
        }
        if (torrentService != null) {
            Log.d(TAG, "Calling addTorrent on service for: $magnetUri")
            torrentService?.addTorrent(magnetUri, downloadPath)
            _magnetUriInput.value = "" // Clear input after adding
        } else {
            Log.e(TAG, "TorrentDownloadService is not bound. Cannot add torrent.")
            // Try to start and bind the service, then retry adding the torrent
            startAndBindService(context)
            // A simple retry mechanism, might need more robust handling in a real app
            viewModelScope.launch {
                var retryCount = 0
                while (torrentService == null && retryCount < 5) {
                    delay(500) // Wait for service to bind
                    retryCount++
                }
                if (torrentService != null) {
                    torrentService?.addTorrent(magnetUri, downloadPath)
                    _magnetUriInput.value = ""
                } else {
                    Log.e(TAG, "Failed to add torrent after retries: Service still not bound.")
                }
            }
        }
    }

    /**
     * Removes a torrent from the download session.
     *
     * @param magnetUri The magnet URI of the torrent to remove.
     */
    fun removeTorrent(magnetUri: String) {
        if (torrentService != null) {
            Log.d(TAG, "Calling removeTorrent on service for: $magnetUri")
            torrentService?.removeTorrent(magnetUri)
        } else {
            Log.e(TAG, "TorrentDownloadService is not bound. Cannot remove torrent.")
        }
    }

    /**
     * Lifecycle method: Called when the ViewModel is no longer used and will be destroyed.
     * Ensures the service is unbound to prevent memory leaks.
     */
    override fun onCleared() {
        super.onCleared()
        // It's generally better to unbind in onStop/onDestroy of the Activity/Fragment
        // but this is a fallback for when the ViewModel is cleared.
        Log.d(TAG, "ViewModel onCleared. Ensure service is unbound by Activity/Fragment.")
    }
}
