plugins {
    // Android Gradle Plugin (AGP) version. For compileSdk 34, AGP 8.2.2 or newer is stable.
    id("com.android.application") version "8.11.0" apply false
    id("com.android.library") version "8.11.0" apply false

    // Kotlin Gradle Plugin version. Should be compatible with your Compose compiler extension.
    // Kotlin 1.9.22 is the latest stable for Compose compiler extension 1.5.1.
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    // The 'kotlin-parcelize' plugin does NOT need to be declared here.
    // It is applied directly in the module's build.gradle.kts.
    // id("com.google.dagger.hilt.android") version "2.51.1" apply false // Uncomment if using Hilt
}
