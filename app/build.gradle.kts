import java.net.URI // Explicitly import java.net.URI - This line is crucial for resolving 'net' reference

// Top-level plugins block
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize") // Add this for @Parcelize annotation
    id("kotlin-kapt") // ADDED: KAPT plugin for annotation processing (e.g., Hilt)
    // If using Hilt, you might also need to apply the Hilt Gradle plugin here
    // id("com.google.dagger.hilt.android") // Apply Hilt plugin if using Hilt
}

// Android configuration block
android {
    namespace = "com.jmr.mediapowerhouse"
    compileSdk = 35 // Keep at 34 for now, can be updated to 35 if needed later

    defaultConfig {
        applicationId = "com.jmr.mediapowerhouse"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        // IMPORTANT: Replace with your actual Spotify Developer Dashboard values.
        // The spotifyRedirectUri MUST match the Redirect URI you configured in your Spotify Developer Dashboard.
        // Example: "your-app-scheme://callback" (e.g., "mediapowerhouse://callback")
        val spotifyRedirectUri =
            "mediapowerhouse://callback" // <-- REPLACE THIS WITH YOUR ACTUAL REDIRECT URI (e.g., "mediapowerhouse://callback")
        val spotifyClientId =
            "9d9354f061d7408f8e0a4c5a6dd592eb" // <-- REPLACE THIS WITH YOUR ACTUAL CLIENT ID

        // Define BuildConfig fields for use in Kotlin code (e.g., SpotifyViewModel)
        buildConfigField("String", "SPOTIFY_CLIENT_ID", "\"$spotifyClientId\"")
        buildConfigField("String", "SPOTIFY_REDIRECT_URI", "\"$spotifyRedirectUri\"")

        // Extract scheme and host from the redirect URI for Manifest placeholders.
        // This ensures the AndroidManifest.xml correctly registers the URI scheme.
        // Ensure java.net.URI is correctly imported at the top of the file.
        val parsedUri = URI(spotifyRedirectUri) // 'URI' should now be resolved
        val spotifyScheme = parsedUri.scheme
        val spotifyHost = parsedUri.host

        // Ensure scheme and host are not null before assigning to manifestPlaceholders
        if (spotifyScheme != null && spotifyHost != null) {
            manifestPlaceholders["spotifyScheme"] = spotifyScheme
            manifestPlaceholders["spotifyHost"] = spotifyHost
        } else {
            println("WARNING: Spotify Redirect URI is malformed or missing scheme/host. Spotify integration may not work.")
            manifestPlaceholders["spotifyScheme"] = "" // Provide a fallback
            manifestPlaceholders["spotifyHost"] = ""   // Provide a fallback
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true // Enable BuildConfig generation for Spotify credentials
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1" // Keep consistent with Compose BOM
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    // For libtorrent4j native libraries - ensures correct ABI distribution
    splits {
        abi {
            isEnable = true // CORRECTED: Use 'isEnable'
            reset()
            include("armeabi-v7a", "arm64-v8a", "x86", "x86_64") // Include common ABIs
            isUniversalApk = true // CORRECTED: Use 'isUniversalApk'
        }
    }
}

// Dependencies block
dependencies {
    // Compose BOM (Bill of Materials) for consistent Compose versions
    implementation(platform("androidx.compose:compose-bom:2024.06.00")) // Updated BOM version

    // Compose UI and Material3 dependencies
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Media3 ExoPlayer for audio/video playback
    implementation("androidx.media3:media3-exoplayer:1.3.1")
    implementation("androidx.media3:media3-ui:1.3.1")
    implementation("androidx.media3:media3-session:1.3.1")

    // Accompanist Permissions for simplified runtime permission handling
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")

    // Coil for loading video thumbnails and images
    implementation("io.coil-kt:coil-compose:2.6.0")

    // libtorrent4j for BitTorrent client functionality - IMPORTANT!
    implementation("com.frostwire:libtorrent4j:2.0.9-26") // Use the specified version or check for the latest stable

    // Spotify SDK (Corrected declarations)
    implementation("com.spotify.android:app-remote:latest.integration") // Spotify App Remote SDK
    implementation("com.spotify.android:auth:latest.integration") // Spotify Authentication Library

    // GSON (if required for other parts of your project, ensure it's here)
    // implementation("com.google.code.gson:gson:2.10.1") // Uncomment if GSON is explicitly needed

    // Debugging tools for Compose
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Test implementations
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.06.00")) // Latest stable as of June 2025
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // For ViewModel utilities for Compose Navigation (if you use Hilt with Navigation)
    // If not using Hilt, this can be removed, but it's good practice to include if planning for future Hilt integration
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // For dependency injection (Hilt) - Uncommented and added kapt
    implementation("com.google.dagger:hilt-android:2.56.2") // Using 2.56.2 as per your error log
    kapt("com.google.dagger:hilt-android-compiler:2.56.2") // ADDED: KAPT annotation processor
}
