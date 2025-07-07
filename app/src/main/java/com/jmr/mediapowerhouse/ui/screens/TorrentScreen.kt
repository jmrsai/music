package com.jmr.mediapowerhouse.ui.screens

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.jmr.mediapowerhouse.ui.components.GlassmorphismCard
import com.jmr.mediapowerhouse.ui.components.ToastMessage
import com.jmr.mediapowerhouse.viewmodel.DownloadViewModel
import com.jmr.mediapowerhouse.viewmodel.ThemeViewModel
import java.io.File
import kotlin.random.Random

/**
 * Composable screen for managing torrent downloads.
 * Allows users to add magnet links and view the progress of active downloads.
 * Note: Actual torrent client logic is handled by a Service, this UI simulates interaction.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TorrentScreen(
    modifier: Modifier = Modifier,
    downloadViewModel: DownloadViewModel = viewModel(),
    themeViewModel: ThemeViewModel = viewModel()
) {
    val context = LocalContext.current
    var magnetUriInput by remember { mutableStateOf("") }

    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }

    // Request POST_NOTIFICATIONS permission for Android 13+
    val notificationPermissionState =
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)

    // Request READ_EXTERNAL_STORAGE permission for older Android versions
    val readExternalStoragePermissionState =
        rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)

    // Launcher for requesting MANAGE_EXTERNAL_STORAGE for Android 11+
    val storagePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                toastMessage = "All files access granted!"
                showToast = true
            } else {
                toastMessage = "All files access denied. Downloads might fail."
                showToast = true
            }
        }
    }

    // Lifecycle effect to request permissions when the screen is first shown
    DisposableEffect(Unit) {
        // Request permissions when the screen is first shown
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!notificationPermissionState.status.isGranted) {
                notificationPermissionState.launchPermissionRequest()
            }
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) { // For Android 10 and below for READ_EXTERNAL_STORAGE
            if (!readExternalStoragePermissionState.status.isGranted) {
                readExternalStoragePermissionState.launchPermissionRequest()
            }
        } else { // For Android 11 (R) and above, MANAGE_EXTERNAL_STORAGE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    // Direct user to settings to grant MANAGE_EXTERNAL_STORAGE
                    val intent =
                        android.content.Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.data = android.net.Uri.parse("package:${context.packageName}")
                    storagePermissionLauncher.launch(intent)
                }
            }
        }
        onDispose { }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Torrent Client",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        GlassmorphismCard(modifier = Modifier.fillMaxWidth(), themeViewModel = themeViewModel) {
            Text(
                text = "Add Magnet Link",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            OutlinedTextField(
                value = magnetUriInput,
                onValueChange = { magnetUriInput = it },
                label = { Text("Magnet URI") },
                placeholder = { Text("Enter magnet link here...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (magnetUriInput.isNotBlank()) {
                        val simulatedSize =
                            Random.nextLong(100_000_000, 1_100_000_000) // 100MB - 1.1GB
                        downloadViewModel.addDownload(
                            name = "Torrent: ${magnetUriInput.take(30)}...",
                            type = "Torrent",
                            size = simulatedSize
                        )
                        toastMessage = "Torrent download started!"
                        showToast = true
                        magnetUriInput = "" // Clear input
                    } else {
                        toastMessage = "Please enter a Magnet URI."
                        showToast = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = magnetUriInput.isNotBlank()
            ) {
                Text("Add Torrent")
            }
        }
    }

    if (showToast) {
        ToastMessage(message = toastMessage) {
            showToast = false
        }
    }
}

/**
 * Helper function to get the app-specific download directory.
 * This is generally preferred over public storage for app-specific files.
 */
private fun getAppDownloadDir(context: Context): File {
    // Using context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
    // ensures the files are stored in an app-specific directory that is
    // automatically cleaned up when the app is uninstalled.
    // This does NOT require WRITE_EXTERNAL_STORAGE permission on Android 10+.
    val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "torrents")
    if (!dir.exists()) {
        dir.mkdirs() // Create the directory if it doesn't exist
    }
    return dir
}
