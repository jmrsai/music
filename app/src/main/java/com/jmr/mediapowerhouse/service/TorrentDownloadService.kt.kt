package com.jmr.mediapowerhouse.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.jmr.mediapowerhouse.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * A basic Service to simulate torrent downloading in the background.
 * In a real application, this service would integrate with a torrent library
 * (like libtorrent4j) to manage actual peer connections, file downloads, etc.
 */
class TorrentDownloadService : Service() {

    private val binder = TorrentDownloadServiceBinder()
    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())

    // Map of downloadId to its coroutine job, allowing individual download control
    private val activeDownloads = mutableMapOf<Long, Job>()

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "torrent_download_channel"
        const val NOTIFICATION_CHANNEL_NAME = "Torrent Downloads"
        const val NOTIFICATION_ID = 101 // Unique ID for the foreground notification
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        Log.d("TorrentService", "TorrentDownloadService created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("TorrentService", "onStartCommand called with startId: $startId")
        // Start the service in the foreground immediately
        startForeground(
            NOTIFICATION_ID,
            createNotification("Torrent service running...", 0).build()
        )
        return START_STICKY // Service will be restarted if killed by the system
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d("TorrentService", "onBind called")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d("TorrentService", "onUnbind called")
        return true // Allow re-binding later
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("TorrentService", "TorrentDownloadService destroyed")
        activeDownloads.values.forEach { it.cancel() } // Cancel all active download jobs
        serviceScope.cancel() // Cancel the service's coroutine scope
    }

    /**
     * Starts a simulated torrent download.
     * In a real implementation, this would initiate the actual torrent download process
     * using `libtorrent4j`. The callbacks would be triggered by `libtorrent4j` events.
     *
     * @param downloadId A unique ID for this download.
     * @param fileName The name of the file being downloaded.
     * @param totalSize The total size of the download in bytes.
     * @param onProgressUpdate Callback to update the UI with progress (0-100).
     * @param onStatusUpdate Callback to update the UI with status messages.
     */
    fun startSimulatedTorrentDownload(
        downloadId: Long,
        fileName: String,
        totalSize: Long,
        onProgressUpdate: (Long, Float) -> Unit,
        onStatusUpdate: (Long, String) -> Unit
    ) {
        if (activeDownloads.containsKey(downloadId)) {
            Log.d("TorrentService", "Download $downloadId already active.")
            return
        }

        // --- Placeholder for actual libtorrent4j integration ---
        // In a real scenario, you would initialize libtorrent4j here,
        // add the torrent, and set up listeners for its events.
        // Example (conceptual):
        // val session = SessionManager.getInstance(applicationContext)
        // val torrentHandle = session.addTorrent(magnetUri)
        // torrentHandle.addListener(object : TorrentListener {
        //     override fun onProgress(progress: Float) { onProgressUpdate(downloadId, progress) }
        //     override fun onStatus(status: String) { onStatusUpdate(downloadId, status) }
        //     // ... other callbacks
        // })
        // ----------------------------------------------------

        val job = serviceScope.launch {
            Log.d("TorrentService", "Starting simulated download for $fileName (ID: $downloadId)")
            onStatusUpdate(downloadId, "Connecting to peers...")
            delay(2000) // Simulate connecting phase

            var currentProgress = 0f
            val incrementPerSecond = Random.nextFloat() * 2 + 1 // Simulate 1-3% progress per second

            while (currentProgress < 100f) {
                currentProgress = (currentProgress + incrementPerSecond).coerceAtMost(100f)
                onProgressUpdate(downloadId, currentProgress)
                onStatusUpdate(downloadId, "Downloading: ${currentProgress.toInt()}%")
                updateNotification("Downloading: $fileName", currentProgress.toInt())
                delay(1000) // Simulate download tick
            }
            onProgressUpdate(downloadId, 100f)
            onStatusUpdate(downloadId, "Completed")
            updateNotification("Download Complete: $fileName", 100)
            Log.d("TorrentService", "Simulated download for $fileName (ID: $downloadId) completed.")
            activeDownloads.remove(downloadId) // Remove job on completion
        }
        activeDownloads[downloadId] = job
    }

    /**
     * Stops a simulated torrent download.
     * In a real implementation, this would tell `libtorrent4j` to stop the torrent.
     *
     * @param downloadId The ID of the download to stop.
     */
    fun stopSimulatedTorrentDownload(downloadId: Long) {
        activeDownloads[downloadId]?.cancel()
        activeDownloads.remove(downloadId)
        Log.d("TorrentService", "Simulated download for ID: $downloadId stopped.")
        updateNotification("Download stopped.", 0) // Update notification to a generic state
        // --- Placeholder for actual libtorrent4j integration ---
        // session.removeTorrent(torrentHandle)
        // ----------------------------------------------------
    }

    /**
     * Creates a notification channel for Android O and above.
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    /**
     * Creates and updates the foreground service notification.
     */
    private fun createNotification(contentText: String, progress: Int): NotificationCompat.Builder {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Media Powerhouse Downloader")
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.stat_sys_download) // Generic download icon
            .setContentIntent(pendingIntent)
            .setOngoing(progress < 100) // Make ongoing if not completed
            .setProgress(100, progress, false) // Max, current, indeterminate
    }

    /**
     * Updates the existing foreground service notification.
     */
    private fun updateNotification(contentText: String, progress: Int) {
        val notification = createNotification(contentText, progress).build()
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    /**
     * Binder class for clients to interact with the service.
     */
    inner class TorrentDownloadServiceBinder : Binder() {
        fun getService(): TorrentDownloadService = this@TorrentDownloadService
    }
}
