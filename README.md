# mediapowerhouse

MediaPowerhouse is an Android application built with Jetpack Compose that allows users to browse and
play audio and video files stored on their device. It leverages Android's Media3 library for robust
media playback capabilities and follows modern Android development practices.

## Features

* **Media Browse:** Displays lists of audio and video files found on the device.
* **Media Playback:** Plays selected audio and video files using `Media3 ExoPlayer`.
* **Permissions Handling:** Gracefully requests and manages necessary storage permissions for media
  access.
* **Search Functionality:** Allows users to search through their media files.
* **Modern UI:** Built entirely with Jetpack Compose for a declarative and responsive user
  interface.
* **ViewModel Architecture:** Utilizes `ViewModel` and `StateFlow` for managing UI state and data.

## Technologies Used

* **Kotlin:** Primary programming language.
* **Jetpack Compose:** Modern toolkit for building native Android UI.
* **AndroidX Media3 (ExoPlayer):** Powerful and extensible media playback library.
* **Jetpack Navigation Compose:** For navigating between different screens in the app.
* **Jetpack Lifecycle (ViewModel):** Manages UI-related data in a lifecycle-conscious way.
* **Kotlin Coroutines & Flow:** For asynchronous operations and reactive data streams.
* **Accompanist Permissions:** Simplifies runtime permissions requests.

## Project Structure Highlights

The project follows a standard Android architecture with clear separation of concerns:

* **`MainActivity.kt`**: The main entry point of the application, handling permissions, setting up
  the `MediaController`, and orchestrating the Compose UI.
* **`viewmodel/MainViewModel.kt`**: Manages the application's data and state, including loading
  media files from the device, filtering them based on search queries, and providing them to the UI.
  It uses `Application` context for media store access.
* **`viewmodel/MainViewModelFactory.kt`**: A custom factory to instantiate `MainViewModel` with the
  `Application` context.
* **`data/AppMediaItem.kt`**: Data class representing a media item with properties like ID, URI,
  title, artist, album, and duration.
* **`service/PlaybackService.kt`**: (Inferred) Likely responsible for background media playback
  using `MediaSessionService` from Media3.
* **`ui/screens/MediaListScreen.kt`**: (Inferred) Composable for displaying a list of media items.
* **`ui/screens/PlayerScreen.kt`**: (Inferred) Composable for displaying the media playback
  interface.

## Getting Started

### Prerequisites

* Android Studio (latest stable version recommended)
* Android SDK

### Installation

1. **Clone the repository:**
   ```bash
   git clone <repository_url_here>
   cd mediapowerhouse
   ```
2. **Open in Android Studio:**
   Open the cloned project in Android Studio.
3. **Sync Gradle:**
   Android Studio should automatically sync the Gradle files. If not, click `File` >
   `Sync Project with Gradle Files`.
4. **Build and Run:**
   Click the `Run` button (green play icon) in Android Studio, or go to `Build` > `Make Project` and
   then `Run` > `Run 'app'`.

### Permissions

On first launch, the app will request necessary storage permissions to access media files. Please
grant these permissions for the app to function correctly.

## Contribution

(If applicable, add guidelines for contributing to the project here.)

## License

(If applicable, add your project's license information here.)# music
