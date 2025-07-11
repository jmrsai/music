pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal() // This is crucial for resolving Android Gradle Plugins
    }
}
dependencyResolutionManagement {
    // This line enforces that all repositories MUST be declared here.
    // If you add repositories in project-level build.gradle.kts, it will cause an error.
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Add the FrostWire Maven repository here for libtorrent4j, explicitly converting URL to URI
        maven { url = uri("https://repo.frostwire.com/maven2") }
    }
}
rootProject.name = "MediaPowerhouse"
include(":app")
