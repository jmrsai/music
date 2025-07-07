package com.jmr.mediapowerhouse

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share2
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.jmr.mediapowerhouse.service.PlaybackService
import com.jmr.mediapowerhouse.ui.screens.DownloadsScreen
import com.jmr.mediapowerhouse.ui.screens.HomeScreen
import com.jmr.mediapowerhouse.ui.screens.MediaListScreen
import com.jmr.mediapowerhouse.ui.screens.MusicScreen
import com.jmr.mediapowerhouse.ui.screens.PlayerScreen
import com.jmr.mediapowerhouse.ui.screens.SettingsScreen
import com.jmr.mediapowerhouse.ui.screens.SocialMediaScreen
import com.jmr.mediapowerhouse.ui.screens.TorrentScreen
import com.jmr.mediapowerhouse.ui.screens.YouTubeScreen
import com.jmr.mediapowerhouse.ui.theme.MediaPowerhouseTheme
import com.jmr.mediapowerhouse.viewmodel.DownloadViewModel
import com.jmr.mediapowerhouse.viewmodel.MediaViewModel
import com.jmr.mediapowerhouse.viewmodel.PlayerViewModel
import com.jmr.mediapowerhouse.viewmodel.ThemeViewModel

class MainActivity : ComponentActivity() {

    private lateinit var mediaControllerFuture: ListenableFuture<MediaController>
    private val mediaControllerConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mediaControllerFuture = MediaController.Builder(
                this@MainActivity,
                SessionToken(
                    this@MainActivity,
                    ComponentName(this@MainActivity, PlaybackService::class.java)
                )
            ).buildAsync()
            Log.d("MainActivity", "MediaController connected")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d("MainActivity", "MediaController disconnected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false) // Enable edge-to-edge display
        enableEdgeToEdge()

        // Bind to the PlaybackService to get the MediaController
        val sessionToken = SessionToken(this, ComponentName(this, PlaybackService::class.java))
        mediaControllerFuture = MediaController.Builder(this, sessionToken).buildAsync()

        setContent {
            // Instantiate ViewModels
            val mediaViewModel: MediaViewModel = viewModel()
            val playerViewModel: PlayerViewModel = viewModel()
            val downloadViewModel: DownloadViewModel = viewModel() // New: DownloadViewModel
            val themeViewModel: ThemeViewModel = viewModel() // New: ThemeViewModel

            MediaPowerhouseTheme(
                darkTheme = themeViewModel.isDarkMode.value // Use theme from ViewModel
            ) {
                val navController = rememberNavController()

                // Connect MediaController to PlayerViewModel
                DisposableEffect(mediaControllerFuture) {
                    val listener = { controller: MediaController ->
                        playerViewModel.setMediaController(controller)
                    }
                    mediaControllerFuture.addListener(listener, MoreExecutors.directExecutor())

                    onDispose {
                        mediaControllerFuture.removeListener(
                            listener,
                            MoreExecutors.directExecutor()
                        )
                        MediaController.releaseFuture(mediaControllerFuture)
                    }
                }

                // Function to play media items
                val onPlayMedia: (List<Uri>, Int) -> Unit = { mediaItems, index ->
                    playerViewModel.playMedia(mediaItems, index)
                    navController.navigate(Screen.Player.route)
                }

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentRoute = navBackStackEntry?.destination?.route

                            // Define navigation items for the bottom bar
                            val items = listOf(
                                Screen.Home,
                                Screen.YouTube,
                                Screen.Torrents,
                                Screen.Downloads,
                                Screen.Settings
                            )

                            items.forEach { screen ->
                                NavigationBarItem(
                                    icon = { Icon(screen.icon, contentDescription = screen.route) },
                                    label = { Text(screen.route.replaceFirstChar { it.uppercase() }) },
                                    selected = currentRoute == screen.route,
                                    onClick = {
                                        navController.navigate(screen.route) {
                                            // Pop up to the start destination of the graph to
                                            // avoid building up a large stack of destinations
                                            // on the back stack as users select items
                                            popUpTo(navController.graph.startDestinationId) {
                                                saveState = true
                                            }
                                            // Avoid multiple copies of the same destination when
                                            // reselecting the same item
                                            launchSingleTop = true
                                            // Restore state when reselecting a previously selected item
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        composable(Screen.Home.route) {
                            HomeScreen(downloadViewModel = downloadViewModel)
                        }
                        composable(Screen.YouTube.route) {
                            YouTubeScreen(downloadViewModel = downloadViewModel)
                        }
                        composable(Screen.SocialMedia.route) {
                            SocialMediaScreen(downloadViewModel = downloadViewModel)
                        }
                        composable(Screen.Torrents.route) {
                            TorrentScreen(downloadViewModel = downloadViewModel)
                        }
                        composable(Screen.Music.route) {
                            MusicScreen(downloadViewModel = downloadViewModel)
                        }
                        composable(Screen.Downloads.route) {
                            DownloadsScreen(downloadViewModel = downloadViewModel)
                        }
                        composable(Screen.Settings.route) {
                            SettingsScreen(
                                themeViewModel = themeViewModel,
                                downloadViewModel = downloadViewModel
                            )
                        }
                        // Original media list and player screens (can be integrated or kept separate)
                        composable("music_list") { // Example: If you still want a dedicated music list
                            val audioList by mediaViewModel.filteredAudioList
                            MediaListScreen(
                                mediaItems = audioList,
                                isLoading = mediaViewModel.isLoading.value,
                                searchQuery = mediaViewModel.searchQuery.value,
                                onSearchQueryChange = { mediaViewModel.searchQuery.value = it },
                                onItemClick = { index -> onPlayMedia(audioList, index) }
                            )
                        }
                        composable("videos_list") { // Example: If you still want a dedicated video list
                            val videoList by mediaViewModel.filteredVideoList
                            MediaListScreen(
                                mediaItems = videoList,
                                isLoading = mediaViewModel.isLoading.value,
                                searchQuery = mediaViewModel.searchQuery.value,
                                onSearchQueryChange = { mediaViewModel.searchQuery.value = it },
                                onItemClick = { index -> onPlayMedia(videoList, index) }
                            )
                        }
                        composable(Screen.Player.route) {
                            PlayerScreen(mediaControllerFuture)
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Bind to PlaybackService when activity starts
        val intent = Intent(this, PlaybackService::class.java)
        bindService(intent, mediaControllerConnection, BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        // Release MediaController when activity stops
        MediaController.releaseFuture(mediaControllerFuture)
        unbindService(mediaControllerConnection)
    }
}

// Define sealed class for navigation destinations and their icons
sealed class Screen(val route: String, val icon: ImageVector) {
    object Home : Screen("home", Icons.Default.Home)
    object YouTube : Screen("youtube", Icons.Default.Videocam) // Using Videocam for YouTube
    object SocialMedia : Screen("socialMedia", Icons.Default.Share2)
    object Torrents : Screen("torrents", Icons.Default.Download) // Using Download for Torrents
    object Music : Screen("music", Icons.Default.MusicNote) // Using MusicNote for Music
    object Downloads :
        Screen("downloads", Icons.Default.Download) // Re-using Download for Downloads list

    object Settings : Screen("settings", Icons.Default.Settings)
    object Player : Screen("player", Icons.Default.PlayArrow) // Player screen (not in bottom bar)
}
