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
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jmr.mediapowerhouse.ui.components.CustomSwitch
import com.jmr.mediapowerhouse.ui.components.GlassmorphismCard
import com.jmr.mediapowerhouse.ui.components.ToastMessage
import com.jmr.mediapowerhouse.viewmodel.DownloadViewModel
import com.jmr.mediapowerhouse.viewmodel.ThemeViewModel

/**
 * Composable screen for application settings.
 * Allows users to toggle dark mode, manage download history, and view app info.
 */
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    themeViewModel: ThemeViewModel = viewModel(),
    downloadViewModel: DownloadViewModel = viewModel()
) {
    val isDarkMode by themeViewModel.isDarkMode.collectAsStateWithLifecycle()
    val showDownloadHistory by downloadViewModel.showDownloadHistory.collectAsStateWithLifecycle()

    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Theme Settings
        GlassmorphismCard(modifier = Modifier.fillMaxWidth(), themeViewModel = themeViewModel) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Palette,
                    contentDescription = "Theme",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Dark Mode",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                CustomSwitch(
                    checked = isDarkMode,
                    onCheckedChange = {
                        themeViewModel.toggleDarkMode()
                        toastMessage = if (it) "Dark Mode Enabled" else "Dark Mode Disabled"
                        showToast = true
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Download History Settings
        GlassmorphismCard(modifier = Modifier.fillMaxWidth(), themeViewModel = themeViewModel) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.History,
                    contentDescription = "History",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Show Download History",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                CustomSwitch(
                    checked = showDownloadHistory,
                    onCheckedChange = {
                        downloadViewModel.toggleShowDownloadHistory()
                        toastMessage =
                            if (it) "Download History Visible" else "Download History Hidden"
                        showToast = true
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    downloadViewModel.clearAllDownloads()
                    toastMessage = "All downloads cleared!"
                    showToast = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.ClearAll, contentDescription = "Clear Downloads")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Clear All Downloads")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // About Section
        GlassmorphismCard(modifier = Modifier.fillMaxWidth(), themeViewModel = themeViewModel) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = "About",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "About Media Powerhouse",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Version: 1.0.0 (Simulated)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(
                text = "Developed by JMR",
                style = MaterialTheme.typography.bodyMedium,
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
