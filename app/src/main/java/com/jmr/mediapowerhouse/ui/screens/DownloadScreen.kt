package com.jmr.mediapowerhouse.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PauseCircleFilled
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Share2
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jmr.mediapowerhouse.ui.components.GlassmorphismCard
import com.jmr.mediapowerhouse.viewmodel.DownloadItem
import com.jmr.mediapowerhouse.viewmodel.DownloadViewModel

/**
 * The Downloads screen.
 * Displays a list of all simulated downloads with their progress and status.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DownloadsScreen(
    modifier: Modifier = Modifier,
    downloadViewModel: DownloadViewModel = viewModel(),
    themeViewModel: `ThemeViewModel.kt` = viewModel()
) {
    val downloads by downloadViewModel.downloads.collectAsStateWithLifecycle()
    val showDownloadHistory by downloadViewModel.showDownloadHistory.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Downloads",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (!showDownloadHistory) {
            GlassmorphismCard(
                modifier = Modifier.fillMaxWidth(),
                `themeViewModel.kt` = themeViewModel
            ) {
                Text(
                    text = "Download history is currently hidden. You can enable it in Settings.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else if (downloads.isEmpty()) {
            GlassmorphismCard(
                modifier = Modifier.fillMaxWidth(),
                `themeViewModel.kt` = themeViewModel
            ) {
                Text(
                    text = "No downloads yet. Start downloading from YouTube, Social Media, or Torrents!",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(downloads, key = { it.id }) { download ->
                    // Animated visibility for items entering/exiting the list
                    AnimatedVisibility(
                        visible = true, // Always visible once in the list, animation is for initial appearance/removal
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                        exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
                        modifier = Modifier.animateItemPlacement(
                            animationSpec = tween(
                                durationMillis = 300
                            )
                        ) // Animates item reordering
                    ) {
                        DownloadItemCard(
                            download = download,
                            onTogglePauseResume = {
                                if (download.status == "Downloading") {
                                    downloadViewModel.pauseDownload(download.id)
                                } else if (download.status == "Paused") {
                                    downloadViewModel.resumeDownload(download.id)
                                }
                            },
                            onDelete = { downloadViewModel.removeDownload(download.id) },
                            themeViewModel = themeViewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DownloadItemCard(
    download: DownloadItem,
    onTogglePauseResume: () -> Unit,
    onDelete: () -> Unit,
    themeViewModel: `ThemeViewModel.kt`
) {
    GlassmorphismCard(modifier = Modifier.fillMaxWidth(), `themeViewModel.kt` = themeViewModel) {
        Column(modifier = Modifier.animateContentSize()) { // Animate content changes within the card
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = when (download.type) {
                        "YouTube" -> Icons.Default.Movie
                        "Social Media" -> Icons.Default.Share2
                        "Torrent" -> Icons.Default.Download
                        "Audio" -> Icons.Default.LibraryMusic
                        else -> Icons.Default.Info
                    },
                    contentDescription = download.type,
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = download.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1
                    )
                    Text(
                        text = "${download.status} - ${download.progress.toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                // Action buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (download.status == "Downloading" || download.status == "Paused") {
                        IconButton(onClick = onTogglePauseResume) {
                            Icon(
                                imageVector = if (download.status == "Downloading") Icons.Default.PauseCircleFilled else Icons.Default.PlayCircleFilled,
                                contentDescription = if (download.status == "Downloading") "Pause" else "Resume",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = download.progress / 100f,
                modifier = Modifier.fillMaxWidth(),
                color = when (download.status) {
                    "Completed" -> Icons.Default.CheckCircle
                    "Failed" -> Icons.Default.Error
                    else -> MaterialTheme.colorScheme.primary // Default for active/paused
                }.let {
                    // This is a workaround as Icon is not a Color.
                    // You'd typically use a specific color for progress.
                    // For now, let's just use primary color for progress bar.
                    MaterialTheme.colorScheme.primary
                }
            )
            // Display status text below progress bar
            Text(
                text = when (download.status) {
                    "Completed" -> "Download finished."
                    "Failed" -> "Download failed. Please try again."
                    "Paused" -> "Download paused."
                    "Downloading" -> "Downloading..."
                    else -> "Pending..."
                },
                style = MaterialTheme.typography.bodySmall,
                color = when (download.status) {
                    "Completed" -> MaterialTheme.colorScheme.tertiary
                    "Failed" -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                },
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
