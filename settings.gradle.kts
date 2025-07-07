pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS) // This line enforces central repository declaration
    repositories {
        google()
        mavenCentral()
        // Add the FrostWire Maven repository here for libtorrent4j, explicitly converting URL to URI
        maven { url = uri("https://repo.frostwire.com/maven2") }
    }
}
rootProject.name = "MediaPowerhouse"
include(":app")
