package com.jmr.mediapowerhouse.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jmr.mediapowerhouse.ui.components.GlassmorphismCard
import com.jmr.mediapowerhouse.ui.components.ToastMessage
import com.jmr.mediapowerhouse.viewmodel.DownloadViewModel
import com.jmr.mediapowerhouse.viewmodel.ThemeViewModel
import kotlin.random.Random

/**
 * The Social Media Downloader screen.
 * Allows users to input a social media URL and select the platform (simulated) for direct download.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialMediaScreen(
    modifier: Modifier = Modifier,
    downloadViewModel: DownloadViewModel = viewModel(),
    themeViewModel: ThemeViewModel = viewModel()
) {
    var url by remember { mutableStateOf("") }
    val platforms = listOf("Instagram", "TikTok", "Twitter", "Facebook")
    var selectedPlatform by remember { mutableStateOf(platforms[0]) } // Default to Instagram
    var expanded by remember { mutableStateOf(false) }

    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Social Media Downloader",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        GlassmorphismCard(modifier = Modifier.fillMaxWidth(), themeViewModel = themeViewModel) {
            Text(
                text = "Download from URL",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("Social Media Post/Video URL") },
                placeholder = { Text("Enter social media URL here...") },
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
                    value = selectedPlatform,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Platform") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    platforms.forEach { platform ->
                        DropdownMenuItem(
                            text = { Text(platform) },
                            onClick = {
                                selectedPlatform = platform
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (url.isNotBlank()) {
                        val simulatedSize = Random.nextLong(10_000_000, 210_000_000) // 10MB - 210MB
                        downloadViewModel.addDownload(
                            name = "$selectedPlatform: ${url.take(30)}...",
                            type = "Social Media",
                            size = simulatedSize
                        )
                        toastMessage = "Download started for $selectedPlatform content!"
                        showToast = true
                        url = "" // Clear input
                    } else {
                        toastMessage = "Please enter a social media URL."
                        showToast = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = url.isNotBlank()
            ) {
                Text("Start Download")
            }
        }
    }

    if (showToast) {
        ToastMessage(message = toastMessage) {
            showToast = false
        }
    }
}
