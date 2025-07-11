package com.jmr.mediapowerhouse.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jmr.mediapowerhouse.ui.components.GlassmorphismCard
import com.jmr.mediapowerhouse.viewmodel.DownloadViewModel
import kotlinx.coroutines.delay

/**
 * The Home screen of the application.
 * Displays a dashboard with simulated download statistics and recent activity.
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    downloadViewModel: DownloadViewModel = viewModel(),
    themeViewModel: `ThemeViewModel.kt` = viewModel()
) {
    val totalDownloads by downloadViewModel.totalDownloads.collectAsStateWithLifecycle()
    val activeDownloads by downloadViewModel.activeDownloads.collectAsStateWithLifecycle()
    val completedDownloads by downloadViewModel.completedDownloads.collectAsStateWithLifecycle()
    val totalDownloadedSize by downloadViewModel.totalDownloadedSize.collectAsStateWithLifecycle()
    val recentDownloads by downloadViewModel.recentDownloads.collectAsStateWithLifecycle()

    // State for animating the "Welcome" text visibility
    var showWelcomeText by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(300) // Small delay for initial animation
        showWelcomeText = true
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .animateContentSize() // Animates size changes in the column
    ) {
        AnimatedVisibility(
            visible = showWelcomeText,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { -it / 2 })
        ) {
            Text(
                text = "Welcome to Media Powerhouse!",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }


        // Download Statistics Card
        GlassmorphismCard(
            modifier = Modifier.fillMaxWidth(),
            `themeViewModel.kt` = themeViewModel
        ) {
            Text(
                text = "Download Statistics",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            StatisticRow(label = "Total Downloads", value = totalDownloads.toString())
            StatisticRow(label = "Active Downloads", value = activeDownloads.toString())
            StatisticRow(label = "Completed Downloads", value = completedDownloads.toString())
            StatisticRow(label = "Total Data Downloaded", value = formatBytes(totalDownloadedSize))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Recent Activity Card
        GlassmorphismCard(
            modifier = Modifier.fillMaxWidth(),
            `themeViewModel.kt` = themeViewModel
        ) {
            Text(
                text = "Recent Activity",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (recentDownloads.isEmpty()) {
                Text(
                    text = "No recent downloads. Start downloading something!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            } else {
                Column {
                    recentDownloads.take(3).forEach { download -> // Show up to 3 recent downloads
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
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
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = download.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    maxLines = 1
                                )
                                Text(
                                    text = "${download.status} - ${download.progress.toInt()}%",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                                LinearProgressIndicator(
                                    progress = download.progress / 100f,
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatisticRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * Helper function to format byte size into a human-readable string.
 */
fun formatBytes(bytes: Long): String {
    if (bytes == 0L) return "0 B"
    val units = listOf("B", "KB", "MB", "GB", "TB")
    var value = bytes.toDouble()
    var unitIndex = 0
    while (value >= 1024 && unitIndex < units.size - 1) {
        value /= 1024
        unitIndex++
    }
    return "%.2f %s".format(value, units[unitIndex])
}
