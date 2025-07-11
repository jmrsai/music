package com.jmr.mediapowerhouse.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jmr.mediapowerhouse.BuildConfig
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Track
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Spotify integration.
 * Handles Spotify App Remote connection, authentication, search, and playback.
 */
class `SpotifyViewModel.kt`(application: Application) : AndroidViewModel(application) {

    private var spotifyAppRemote: SpotifyAppRemote? = null
    private val context: Context = application.applicationContext

    // Spotify Client ID and Redirect URI from BuildConfig
    private val CLIENT_ID = BuildConfig.SPOTIFY_CLIENT_ID
    private val REDIRECT_URI = BuildConfig.SPOTIFY_REDIRECT_URI

    // UI State for connection status
    private val _isConnected = MutableStateFlow(false)
    val isConnected = _isConnected.asStateFlow()

    // UI State for current playing track
    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack = _currentTrack.asStateFlow()

    // UI State for search results (simulated for now)
    private val _searchResults = MutableStateFlow<List<String>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    init {
        Log.d(
            "SpotifyViewModel",
            "SpotifyViewModel initialized. Client ID: $CLIENT_ID, Redirect URI: $REDIRECT_URI"
        )
    }

    /**
     * Builds and returns an AuthorizationRequest for Spotify login.
     */
    fun getAuthorizationRequest(): AuthorizationRequest {
        val builder = AuthorizationRequest.Builder(
            CLIENT_ID,
            AuthorizationResponse.Type.TOKEN,
            REDIRECT_URI
        )
        builder.setScopes(arrayOf("app-remote-control", "streaming", "user-read-currently-playing"))
        return builder.build()
    }

    /**
     * Connects to Spotify App Remote after successful authentication.
     */
    fun connectSpotifyAppRemote(accessToken: String) {
        val connectionParams = ConnectionParams.Builder(CLIENT_ID)
            .setRedirectUri(REDIRECT_URI)
            .showAuthView(true) // Show Spotify login if needed
            .build()

        SpotifyAppRemote.connect(context, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                spotifyAppRemote = appRemote
                _isConnected.value = true
                Log.d("SpotifyViewModel", "Spotify App Remote connected!")
                subscribeToPlayerState()
            }

            override fun onFailure(error: Throwable) {
                _isConnected.value = false
                Log.e(
                    "SpotifyViewModel",
                    "Spotify App Remote connection failed: ${error.message}",
                    error
                )
            }
        })
    }

    /**
     * Disconnects from Spotify App Remote.
     */
    fun disconnectSpotifyAppRemote() {
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
            spotifyAppRemote = null
            _isConnected.value = false
            Log.d("SpotifyViewModel", "Spotify App Remote disconnected.")
        }
    }

    /**
     * Subscribes to player state changes to update the UI with the current track.
     */
    private fun subscribeToPlayerState() {
        spotifyAppRemote?.playerApi?.subscribeToPlayerState()?.setEventCallback { playerState ->
            _currentTrack.value = playerState.track
            Log.d(
                "SpotifyViewModel",
                "Current Track: ${playerState.track.name} by ${playerState.track.artist.name}"
            )
        }?.setErrorCallback { error ->
            Log.e("SpotifyViewModel", "Error subscribing to player state: ${error.message}")
        }
    }

    /**
     * Plays a Spotify URI (e.g., track, album, playlist).
     */
    fun playUri(uri: String) {
        spotifyAppRemote?.playerApi?.play(uri)?.setResultCallback {
            Log.d("SpotifyViewModel", "Playing URI: $uri")
        }?.setErrorCallback { error ->
            Log.e("SpotifyViewModel", "Error playing URI: ${error.message}")
        }
    }

    /**
     * Pauses playback.
     */
    fun pause() {
        spotifyAppRemote?.playerApi?.pause()?.setResultCallback {
            Log.d("SpotifyViewModel", "Playback paused")
        }?.setErrorCallback { error ->
            Log.e("SpotifyViewModel", "Error pausing playback: ${error.message}")
        }
    }

    /**
     * Resumes playback.
     */
    fun resume() {
        spotifyAppRemote?.playerApi?.resume()?.setResultCallback {
            Log.d("SpotifyViewModel", "Playback resumed")
        }?.setErrorCallback { error ->
            Log.e("SpotifyViewModel", "Error resuming playback: ${error.message}")
        }
    }

    /**
     * Simulates a Spotify search.
     * In a real app, you'd use the Spotify Web API for search.
     * For now, it just populates dummy results.
     */
    fun searchSpotify(query: String) {
        viewModelScope.launch {
            Log.d("SpotifyViewModel", "Simulating Spotify search for: $query")
            // In a real application, you would use a Spotify Web API client here
            // to perform the search and get actual track/album/artist data.
            // Example: spotifyWebApi.search(query, SearchType.TRACK).execute()

            delay(1000) // Simulate network delay
            _searchResults.value = listOf(
                "Song 1 by Artist A (Simulated)",
                "Song 2 by Artist B (Simulated)",
                "Album X by Artist C (Simulated)",
                "Playlist Y (Simulated)"
            ).filter { it.contains(query, ignoreCase = true) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnectSpotifyAppRemote()
    }
}
