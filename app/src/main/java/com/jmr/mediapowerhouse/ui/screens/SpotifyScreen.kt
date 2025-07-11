package com.jmr.mediapowerhouse.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PauseCircleFilled
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jmr.mediapowerhouse.ui.components.GlassmorphismCard
import com.jmr.mediapowerhouse.ui.components.ToastMessage
import com.spotify.sdk.android.auth.AuthorizationResponse

/**
 * Composable screen for Spotify integration.
 * Allows users to log in to Spotify, search for music, and control playback.
 */
@Composable
fun SpotifyScreen(
    modifier: Modifier = Modifier,
    `spotifyViewModel.kt`: `SpotifyViewModel.kt` = viewModel(),
    themeViewModel: `ThemeViewModel.kt` = viewModel()
) {
    val context = LocalContext.current
    val isConnected by `spotifyViewModel.kt`.isConnected.collectAsStateWithLifecycle()
    val currentTrack by `spotifyViewModel.kt`.currentTrack.collectAsStateWithLifecycle()
    val searchResults by `spotifyViewModel.kt`.searchResults.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }

    // Activity Result Launcher for Spotify Authentication
    val spotifyAuthLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val response = AuthorizationClient.getResponse(result.resultCode, result.data)
        when (response.type) {
            AuthorizationResponse.Type.TOKEN -> {
                // Handle successful token response
                val accessToken = response.accessToken
                toastMessage = "Spotify Connected!"
                showToast = true
                `spotifyViewModel.kt`.connectSpotifyAppRemote(accessToken)
            }

            AuthorizationResponse.Type.ERROR -> {
                // Handle error response
                toastMessage = "Spotify Auth Error: ${response.error}"
                showToast = true
            }

            else -> {
                // Handle other responses
                toastMessage = "Spotify Auth Cancelled."
                showToast = true
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Spotify Integration",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        GlassmorphismCard(
            modifier = Modifier.fillMaxWidth(),
            `themeViewModel.kt` = themeViewModel
        ) {
            if (!isConnected) {
                Text(
                    text = "Connect to Spotify",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Button(
                    onClick = {
                        val request = `spotifyViewModel.kt`.getAuthorizationRequest()
                        spotifyAuthLauncher.launch(
                            AuthorizationClient.create
                                (context as Activity?, request)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Login with Spotify")
                }
            } else {
                Text(
                    text = "Connected to Spotify",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Button(
                    onClick = { `spotifyViewModel.kt`.disconnectSpotifyAppRemote() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Disconnect Spotify")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Current Playing Track
                currentTrack?.let { track ->
                    Text(
                        text = "Now Playing:",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = track.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = track.artist.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { `spotifyViewModel.kt`.resume() }) { // Assuming play/pause state from SDK
                            Icon(
                                imageVector = Icons.Default.PlayCircleFilled,
                                contentDescription = "Play",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        IconButton(onClick = { `spotifyViewModel.kt`.pause() }) {
                            Icon(
                                imageVector = Icons.Default.PauseCircleFilled,
                                contentDescription = "Pause",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        IconButton(onClick = { `spotifyViewModel.kt`.playUri("spotify:track:2takcwJ4tcfPUILaY7YZHQ") }) { // Example track
                            Icon(
                                imageVector = Icons.Default.Shuffle, // Using shuffle for "play example"
                                contentDescription = "Play Example Track",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                } ?: Text(
                    text = "No track currently playing.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Search Section
                Text(
                    text = "Search Spotify",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search songs, artists, albums...") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (searchQuery.isNotBlank()) {
                                `spotifyViewModel.kt`.searchSpotify(searchQuery)
                                toastMessage = "Searching Spotify for \"$searchQuery\"..."
                                showToast = true
                            } else {
                                toastMessage = "Please enter a search query."
                                showToast = true
                            }
                        },
                        enabled = searchQuery.isNotBlank()
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Search Spotify")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Search Results
                if (searchResults.isNotEmpty()) {
                    Text(
                        text = "Search Results:",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LazyColumn(modifier = Modifier.height(200.dp)) { // Fixed height for scrollable results
                        items(searchResults) { result ->
                            Text(
                                text = result,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        // In a real app, clicking a search result would play the track
                                        // For now, just show a toast
                                        toastMessage = "Playing: $result (Simulated)"
                                        showToast = true
                                        // You would use spotifyViewModel.playUri(track.uri) here
                                    }
                                    .padding(vertical = 4.dp)
                            )
                        }
                    }
                } else if (searchQuery.isNotBlank()) {
                    Text(
                        text = "No results found for \"$searchQuery\".",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }

    if (showToast) {
        ToastMessage(message = toastMessage) {
            showToast = false
        }
    }
}
