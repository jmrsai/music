package com.jmr.mediapowerhouse.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jmr.mediapowerhouse.ui.components.CustomSwitch
import com.jmr.mediapowerhouse.ui.components.GlassmorphismCard
import com.jmr.mediapowerhouse.viewmodel.DownloadViewModel

/**
 * The Settings screen of the Media Powerhouse application.
 * Allows users to configure theme settings and download history.
 */
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    themeViewModel: `ThemeViewModel.kt` = viewModel(),
    downloadViewModel: DownloadViewModel = viewModel()
) {
    val isDarkMode by themeViewModel.isDarkMode.collectAsState()
    val enableGlassmorphism by themeViewModel.enableGlassmorphism.collectAsState()
    val isHistoryEnabled by downloadViewModel.isHistoryEnabled.collectAsState()

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

        GlassmorphismCard(
            modifier = Modifier.fillMaxWidth(),
            `themeViewModel.kt` = themeViewModel
        ) {
            // Dark Mode Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Dark Mode", style = MaterialTheme.typography.titleMedium)
                CustomSwitch(
                    checked = isDarkMode,
                    onCheckedChange = { themeViewModel.toggleDarkMode() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Glassmorphism Effect Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Glassmorphism Effect", style = MaterialTheme.typography.titleMedium)
                CustomSwitch(
                    checked = enableGlassmorphism,
                    onCheckedChange = { themeViewModel.toggleGlassmorphism() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Enable Download History Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Enable Download History", style = MaterialTheme.typography.titleMedium)
                CustomSwitch(
                    checked = isHistoryEnabled,
                    onCheckedChange = { downloadViewModel.toggleHistoryEnabled() }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Clear All Downloads Button
            Button(
                onClick = { downloadViewModel.clearAllDownloads() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Clear All Downloads")
            }
        }
    }
}
