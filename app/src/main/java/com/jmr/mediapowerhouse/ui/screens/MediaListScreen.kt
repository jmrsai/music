package com.jmr.mediapowerhouse.ui.screens

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.jmr.mediapowerhouse.ui.components.GlassmorphismCard
import com.jmr.mediapowerhouse.viewmodel.`ThemeViewModel.kt`

/**
 * Composable screen for displaying a list of media items (audio or video).
 * It includes a search bar and displays items with their titles and thumbnails.
 *
 * @param mediaItems The list of media URIs to display.
 * @param isLoading Indicates if the media list is currently being loaded.
 * @param searchQuery The current search query string.
 * @param onSearchQueryChange Callback for when the search query changes.
 * @param onItemClick Callback for when a media item is clicked, providing its index.
 */
@Composable
fun MediaListScreen(
    mediaItems: List<Uri>,
    isLoading: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    themeViewModel: `ThemeViewModel.kt` = viewModel() // Inject ThemeViewModel for card styling
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            label = { Text("Search Media") },
            placeholder = { Text("Enter title to search...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Alignment.CenterVertically
            ) {
                CircularProgressIndicator()
                Text("Loading media...", modifier = Modifier.padding(top = 8.dp))
            }
        } else if (mediaItems.isEmpty()) {
            Text(
                text = "No media found. Check your device storage or search criteria.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(mediaItems) { index, uri ->
                    MediaListItem(
                        uri = uri,
                        onClick = { onItemClick(index) },
                        themeViewModel = themeViewModel // Pass themeViewModel
                    )
                }
            }
        }
    }
}

/**
 * Composable for a single media list item.
 * Displays a thumbnail and title for a media URI.
 *
 * @param uri The URI of the media item.
 * @param onClick Callback for when the item is clicked.
 */
@Composable
fun MediaListItem(
    uri: Uri,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    themeViewModel: `ThemeViewModel.kt` = viewModel() // Inject ThemeViewModel for card styling
) {
    GlassmorphismCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        `themeViewModel.kt` = themeViewModel
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            // Thumbnail (using Coil for image loading)
            AsyncImage(
                model = uri,
                contentDescription = "Media Thumbnail",
                modifier = Modifier
                    .size(64.dp)
                    .padding(end = 12.dp),
                contentScale = ContentScale.Crop
            )
            // Media Title (using the last path segment as a placeholder title)
            Text(
                text = uri.lastPathSegment ?: "Unknown Media",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
