package com.jmr.mediapowerhouse.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jmr.mediapowerhouse.ui.components.GlassmorphismCard
import com.jmr.mediapowerhouse.ui.components.ToastMessage
import com.jmr.mediapowerhouse.viewmodel.DownloadViewModel
import com.jmr.mediapowerhouse.viewmodel.ThemeViewModel
import kotlin.random.Random

/**
 * The Music Player & Downloader screen.
 * Allows users to search for and stream/download music (simulated).
 */
@Composable
fun MusicScreen(
    modifier: Modifier = Modifier,
    downloadViewModel: DownloadViewModel = viewModel(),
    themeViewModel: ThemeViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var isPlaying by remember { mutableStateOf(false) }

    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Music Player & Downloader",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        GlassmorphismCard(modifier = Modifier.fillMaxWidth(), themeViewModel = themeViewModel) {
            Text(
                text = "Search & Stream",
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
                    label = { Text("Search for music...") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (searchQuery.isNotBlank()) {
                            toastMessage = "Searching for \"$searchQuery\"... (Simulated)"
                            showToast = true
                            // Simulate search results here
                        } else {
                            toastMessage = "Please enter a search query."
                            showToast = true
                        }
                    },
                    enabled = searchQuery.isNotBlank()
                ) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        isPlaying = !isPlaying
                        toastMessage =
                            if (isPlaying) "Music Playing (Simulated)" else "Music Paused (Simulated)"
                        showToast = true
                    }
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(24.dp))
                Button(
                    onClick = {
                        if (searchQuery.isNotBlank()) {
                            val simulatedSize = Random.nextLong(2_000_000, 12_000_000) // 2MB - 12MB
                            downloadViewModel.addDownload(
                                name = "Music: $searchQuery (Simulated)",
                                type = "Audio",
                                size = simulatedSize
                            )
                            toastMessage = "Music download started!"
                            showToast = true
                            searchQuery = "" // Clear input
                        } else {
                            toastMessage = "Please search for music first."
                            showToast = true
                        }
                    },
                    enabled = searchQuery.isNotBlank()
                ) {
                    Icon(Icons.Default.Download, contentDescription = "Download Music")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Download")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isPlaying) "Now playing: Simulated Song" else "Ready to play music",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }

    if (showToast) {
        ToastMessage(message = toastMessage) {
            showToast = false
        }
    }
}
