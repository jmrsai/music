// Top-level plugins block for the app module
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize") // Apply parcelize plugin here
}

// Android configuration block
android {
    namespace = "com.jmr.mediapowerhouse"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.jmr.mediapowerhouse"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
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
            isEnable = true
            reset()
            include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
            isUniversalApk = true
        }
    }
}

// Dependencies block
dependencies {
    // Compose BOM (Bill of Materials) for consistent Compose versions
    implementation(platform("androidx.compose:compose-bom:2025.06.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.1")
    implementation("androidx.navigation:navigation-compose:2.9.1")

    // Media3 ExoPlayer for audio/video playback
    implementation("androidx.media3:media3-exoplayer:1.7.1")
    implementation("androidx.media3:media3-ui:1.7.1")
    implementation("androidx.media3:media3-session:1.7.1")

    // Accompanist Permissions for simplified runtime permission handling
    implementation("com.google.accompanist:accompanist-permissions:0.37.3")

    // Coil for loading video thumbnails and images
    implementation("io.coil-kt:coil-compose:2.7.0")


    // Debugging tools for Compose
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.9.0-beta02")

    // Test implementations
    androidTestImplementation(platform("androidx.compose:compose-bom:2025.06.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // For ViewModel utilities for Compose Navigation (if you use Hilt with Navigation)
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // For dependency injection (Hilt) - Uncomment if using Hilt
    // implementation("com.google.dagger:hilt-android:2.51.1")
    // kapt("com.google.dagger:hilt-android-compiler:2.51.1")
}
