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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
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
 * The YouTube Downloader screen.
 * Allows users to search for YouTube videos (simulated) and download them,
 * or directly paste a video URL for download.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YouTubeScreen(
    modifier: Modifier = Modifier,
    downloadViewModel: DownloadViewModel = viewModel(),
    themeViewModel: ThemeViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var videoUrlInput by remember { mutableStateOf("") }
    val qualities = listOf("Best Available", "1080p", "720p", "480p", "360p", "Audio Only")
    var selectedQuality by remember { mutableStateOf(qualities[0]) }
    var expanded by remember { mutableStateOf(false) }

    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "YouTube Downloader",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Search Section
        GlassmorphismCard(modifier = Modifier.fillMaxWidth(), themeViewModel = themeViewModel) {
            Text(
                text = "Search YouTube",
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
                    label = { Text("Search videos...") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (searchQuery.isNotBlank()) {
                            toastMessage = "Searching YouTube for \"$searchQuery\"... (Simulated)"
                            showToast = true
                            // Simulate showing search results here
                            // For a real app, this would trigger an API call and display results
                        } else {
                            toastMessage = "Please enter a search query."
                            showToast = true
                        }
                    },
                    enabled = searchQuery.isNotBlank()
                ) {
                    Icon(Icons.Default.Search, contentDescription = "Search YouTube")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Simulated "Browse" button to imply navigating to a video page
            Button(
                onClick = {
                    toastMessage = "Simulating browsing YouTube... (No actual browser)"
                    showToast = true
                    // In a real app, this might open an embedded WebView or a custom video player
                    // that allows browsing related videos.
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Browse YouTube (Simulated)")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Direct Download Link Section
        GlassmorphismCard(modifier = Modifier.fillMaxWidth(), themeViewModel = themeViewModel) {
            Text(
                text = "Download by URL",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            OutlinedTextField(
                value = videoUrlInput,
                onValueChange = { videoUrlInput = it },
                label = { Text("Video URL") },
                placeholder = { Text("Paste YouTube video link here...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedQuality,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Quality") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    qualities.forEach { quality ->
                        DropdownMenuItem(
                            text = { Text(quality) },
                            onClick = {
                                selectedQuality = quality
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (videoUrlInput.isNotBlank()) {
                        val simulatedSize = Random.nextLong(50_000_000, 500_000_000) // 50MB - 500MB
                        downloadViewModel.addDownload(
                            name = "YouTube: ${videoUrlInput.take(30)}... ($selectedQuality)",
                            type = "YouTube",
                            size = simulatedSize
                        )
                        toastMessage = "YouTube download started!"
                        showToast = true
                        videoUrlInput = "" // Clear input
                    } else {
                        toastMessage = "Please enter a YouTube video URL."
                        showToast = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = videoUrlInput.isNotBlank()
            ) {
                Icon(Icons.Default.Download, contentDescription = "Download YouTube Video")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Download Video")
            }
        }
    }

    if (showToast) {
        ToastMessage(message = toastMessage) {
            showToast = false
        }
    }
}
