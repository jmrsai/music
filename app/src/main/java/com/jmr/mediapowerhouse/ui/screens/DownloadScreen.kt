package com.jmr.mediapowerhouse.ui.screens

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Patterns // Import Patterns for URL validation
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File // Import File for file existence check

@Composable
fun DownloadScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var urlText by remember { mutableStateOf("") }
    var downloadId by remember { mutableStateOf<Long?>(null) }
    var downloadProgress by remember { mutableStateOf(0f) } // 0.0 to 1.0
    var downloadStatusText by remember { mutableStateOf("Ready to download") }
    var isDownloading by remember { mutableStateOf(false) }
    var urlError by remember { mutableStateOf(false) } // State for URL validation error

    // BroadcastReceiver to listen for download completion
    val downloadReceiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
                if (id == downloadId) {
                    val downloadManager =
                        context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    val query = DownloadManager.Query().setFilterById(id)
                    val cursor: Cursor? = downloadManager.query(query)
                    if (cursor != null && cursor.moveToFirst()) {
                        val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                        val status = cursor.getInt(statusIndex)
                        when (status) {
                            DownloadManager.STATUS_SUCCESSFUL -> {
                                downloadStatusText = "Download completed!"
                                downloadProgress = 1f
                                isDownloading = false
                                Toast.makeText(context, "Download completed!", Toast.LENGTH_SHORT)
                                    .show()
                                // Optionally, trigger media scan if needed for older Android versions
                                // context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, downloadManager.getUriForDownloadedFile(id)))
                            }

                            DownloadManager.STATUS_FAILED -> {
                                val reasonIndex =
                                    cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
                                val reason = cursor.getInt(reasonIndex)
                                downloadStatusText =
                                    "Download failed: ${getDownloadFailureReason(reason)}"
                                downloadProgress = 0f
                                isDownloading = false
                                Toast.makeText(
                                    context,
                                    "Download failed: ${getDownloadFailureReason(reason)}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                    cursor?.close()
                }
            }
        }
    }

    DisposableEffect(Unit) {
        val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        context.registerReceiver(downloadReceiver, filter)
        onDispose {
            context.unregisterReceiver(downloadReceiver)
        }
    }

    // Coroutine for polling download progress
    val lifecycleOwner = androidx.lifecycle.ViewTreeLifecycleOwner.current
    DisposableEffect(
        downloadId,
        isDownloading
    ) { // Add isDownloading to dependencies to restart job if state changes
        var job: Job? = null
        if (downloadId != null && isDownloading) {
            job = lifecycleOwner?.lifecycleScope?.launch {
                withContext(Dispatchers.IO) {
                    val downloadManager =
                        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    while (isDownloading) {
                        val query = DownloadManager.Query().setFilterById(downloadId!!)
                        var cursor: Cursor? = null // Declare cursor outside try-finally
                        try {
                            cursor = downloadManager.query(query)
                            if (cursor != null && cursor.moveToFirst()) {
                                val bytesDownloadedIndex =
                                    cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                                val bytesTotalIndex =
                                    cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                                val statusIndex =
                                    cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)

                                val bytesDownloaded = cursor.getLong(bytesDownloadedIndex)
                                val bytesTotal = cursor.getLong(bytesTotalIndex)
                                val status = cursor.getInt(statusIndex)

                                if (bytesTotal > 0) {
                                    downloadProgress = bytesDownloaded.toFloat() / bytesTotal
                                }

                                withContext(Dispatchers.Main) { // Update UI on Main thread
                                    when (status) {
                                        DownloadManager.STATUS_PENDING -> downloadStatusText =
                                            "Download pending..."

                                        DownloadManager.STATUS_RUNNING -> downloadStatusText =
                                            "Downloading... (${(downloadProgress * 100).toInt()}%)"

                                        DownloadManager.STATUS_PAUSED -> downloadStatusText =
                                            "Download paused"

                                        DownloadManager.STATUS_SUCCESSFUL -> {
                                            downloadStatusText = "Download completed!"
                                            downloadProgress = 1f
                                            isDownloading = false
                                        }

                                        DownloadManager.STATUS_FAILED -> {
                                            val reasonIndex =
                                                cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
                                            val reason = cursor.getInt(reasonIndex)
                                            downloadStatusText =
                                                "Download failed: ${getDownloadFailureReason(reason)}"
                                            downloadProgress = 0f
                                            isDownloading = false
                                        }
                                    }
                                }
                            }
                        } finally {
                            cursor?.close()
                        }
                        if (isDownloading) delay(1000) // Poll every second
                    }
                }
            }
        }
        onDispose {
            job?.cancel()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Video Downloader",
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = urlText,
            onValueChange = {
                urlText = it
                urlError = !isValidUrl(it) && it.isNotBlank() // Validate on change
            },
            label = { Text("Video URL") },
            placeholder = { Text("e.g., https://example.com/video.mp4") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isDownloading, // Disable input while downloading
            isError = urlError, // Show error state
            supportingText = {
                if (urlError) {
                    Text("Please enter a valid URL (e.g., starts with http/https).")
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (!isNetworkAvailable(context)) {
                    Toast.makeText(context, "No internet connection available.", Toast.LENGTH_LONG)
                        .show()
                    downloadStatusText = "No internet connection."
                    return@Button
                }

                if (urlText.isBlank()) {
                    Toast.makeText(context, "Please enter a URL", Toast.LENGTH_SHORT).show()
                    urlError = true // Mark as error
                    downloadStatusText = "URL cannot be empty."
                } else if (urlError) {
                    Toast.makeText(context, "Please correct the URL format.", Toast.LENGTH_SHORT)
                        .show()
                    downloadStatusText = "Invalid URL format."
                } else if (isDownloading) {
                    Toast.makeText(
                        context,
                        "A download is already in progress.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val newDownloadId = startDownload(context, urlText)
                    if (newDownloadId != null) {
                        downloadId = newDownloadId
                        isDownloading = true
                        downloadProgress = 0f
                        downloadStatusText = "Download queued..."
                    } else {
                        downloadStatusText = "Failed to start download."
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isDownloading // Disable button while downloading
        ) {
            Text(if (isDownloading) "Downloading..." else "Download Video")
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (isDownloading || downloadProgress > 0f) {
            LinearProgressIndicator(
                progress = downloadProgress,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Text(text = downloadStatusText)

        // Optional: Button to cancel download
        if (isDownloading && downloadId != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val downloadManager =
                        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    downloadManager.remove(downloadId!!)
                    downloadStatusText = "Download cancelled."
                    downloadProgress = 0f
                    isDownloading = false
                    downloadId = null
                    Toast.makeText(context, "Download cancelled.", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel Download")
            }
        }
    }
}

// Helper function to validate URL format
private fun isValidUrl(url: String): Boolean {
    return Patterns.WEB_URL.matcher(url).matches()
}

// Helper function to check network availability
private fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    } else {
        @Suppress("DEPRECATION")
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        @Suppress("DEPRECATION")
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}

// Helper function to get more descriptive download failure reasons
private fun getDownloadFailureReason(reason: Int): String {
    return when (reason) {
        DownloadManager.ERROR_CANNOT_RESUME -> "Cannot resume download (e.g., file changed on server)."
        DownloadManager.ERROR_DEVICE_NOT_FOUND -> "No external storage device found."
        DownloadManager.ERROR_FILE_ALREADY_EXISTS -> "File already exists at destination."
        DownloadManager.ERROR_FILE_ERROR -> "File system error."
        DownloadManager.ERROR_HTTP_DATA_ERROR -> "HTTP data error (e.g., server issues)."
        DownloadManager.ERROR_INSUFFICIENT_SPACE -> "Insufficient storage space."
        DownloadManager.ERROR_TOO_MANY_REDIRECTS -> "Too many redirects."
        DownloadManager.ERROR_UNHANDLED_HTTP_CODE -> "Unhandled HTTP code (e.g., 404, 500)."
        DownloadManager.ERROR_UNKNOWN -> "Unknown error."
        else -> "Unknown error code: $reason"
    }
}

private fun startDownload(context: Context, url: String): Long? {
    return try {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(url)

        // Basic check for file extension to suggest a name
        val fileName = if (uri.lastPathSegment != null && uri.lastPathSegment!!.contains(".")) {
            uri.lastPathSegment!!
        } else {
            "downloaded_media_${System.currentTimeMillis()}" // Generic name if no extension
        }

        // Check if file already exists (simple check, not robust against renames)
        val downloadDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val existingFile = File(downloadDir, fileName)
        if (existingFile.exists()) {
            Toast.makeText(
                context,
                "File '$fileName' already exists in Downloads.",
                Toast.LENGTH_LONG
            ).show()
            return null // Prevent re-downloading the same file
        }


        val request = DownloadManager.Request(uri).apply {
            setTitle("Downloading: $fileName")
            setDescription("Downloading media from $url")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            setAllowedOverMetered(true)
            setAllowedOverRoaming(true)
        }
        downloadManager.enqueue(request)
    } catch (e: IllegalArgumentException) {
        // Catch specific URL parsing errors
        Toast.makeText(context, "Invalid URL provided: ${e.message}", Toast.LENGTH_LONG).show()
        e.printStackTrace()
        null
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Failed to queue download: ${e.message}", Toast.LENGTH_LONG).show()
        null
    }
}