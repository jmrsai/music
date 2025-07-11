// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Android Gradle Plugin (AGP) version. For compileSdk 34, AGP 8.2.2 is a stable choice.
    id("com.android.application") version "8.2.2" apply false
    id("com.android.library") version "8.2.2" apply false

    // Kotlin Gradle Plugin version. Should be compatible with your Compose compiler extension (1.5.1).
    // Kotlin 1.9.22 is the latest stable that pairs well with Compose compiler extension 1.5.1.
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    // The 'kotlin-parcelize' plugin does NOT need to be declared here.
    // It is applied directly in the module's build.gradle.kts.
    // id("com.google.dagger.hilt.android") version "2.51.1" apply false // Uncomment if using Hilt
}

// IMPORTANT: No 'repositories' block should be present here due to repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
// in settings.gradle.kts. All repositories must be declared in settings.gradle.kts.
