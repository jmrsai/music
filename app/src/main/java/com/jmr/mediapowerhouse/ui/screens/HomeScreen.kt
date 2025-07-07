package com.jmr.mediapowerhouse.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jmr.mediapowerhouse.ui.components.GlassmorphismCard
import com.jmr.mediapowerhouse.viewmodel.DownloadViewModel
import com.jmr.mediapowerhouse.viewmodel.ThemeViewModel

/**
 * The Home screen of the Media Powerhouse application.
 * Displays a dashboard with download statistics and recent activity.
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    downloadViewModel: DownloadViewModel = viewModel(),
    themeViewModel: ThemeViewModel = viewModel() // Inject ThemeViewModel for card styling
) {
    val downloads by downloadViewModel.downloads.collectAsState()
    val isDarkMode by themeViewModel.isDarkMode.collectAsState()

    val completedDownloads = downloads.filter { it.progress >= 100f }
    val activeDownloads = downloads.filter { it.progress < 100f && it.progress > 0f }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Download Statistics Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlassmorphismCard(modifier = Modifier.weight(1f), themeViewModel = themeViewModel) {
                Text(text = "Total Downloads", style = MaterialTheme.typography.titleMedium)
                Text(text = "${downloads.size}", style = MaterialTheme.typography.displaySmall)
            }
            GlassmorphismCard(modifier = Modifier.weight(1f), themeViewModel = themeViewModel) {
                Text(text = "Completed", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${completedDownloads.size}",
                    style = MaterialTheme.typography.displaySmall
                )
            }
            GlassmorphismCard(modifier = Modifier.weight(1f), themeViewModel = themeViewModel) {
                Text(text = "Active", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${activeDownloads.size}",
                    style = MaterialTheme.typography.displaySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Recent Activity",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Recent Downloads List
        if (downloads.isEmpty()) {
            Text(
                text = "No recent downloads. Start one from other sections!",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDarkMode) Color.LightGray else Color.Gray
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(downloads.take(5), key = { it.id }) { download ->
                    GlassmorphismCard(
                        modifier = Modifier.fillMaxWidth(),
                        themeViewModel = themeViewModel
                    ) {
                        Column {
                            Text(text = download.name, style = MaterialTheme.typography.titleSmall)
                            Text(
                                text = "${download.type} - ${download.status} - ${
                                    formatBytes(
                                        download.size
                                    )
                                }",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isDarkMode) Color.LightGray else Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Helper function to format bytes to human-readable string.
 */
private fun formatBytes(bytes: Long, decimals: Int = 2): String {
    if (bytes == 0L) return "0 Bytes"
    val k = 1024
    val dm = if (decimals < 0) 0 else decimals
    val sizes = arrayOf("Bytes", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB")
    val i = (Math.log(bytes.toDouble()) / Math.log(k.toDouble())).toInt()
    return "%.${dm}f %s".format(bytes / Math.pow(k.toDouble(), i.toDouble()), sizes[i])
}
