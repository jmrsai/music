package com.jmr.mediapowerhouse.service

import android.os.Binder

/**
 * Binder for the TorrentDownloadService.
 * Allows clients to get a reference to the service instance.
 */
class `TorrentDownloadServiceBinder.kt` : Binder() {
    // No additional methods needed here for a simple binder,
    // the service itself exposes the methods.
}
