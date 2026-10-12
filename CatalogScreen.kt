package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.PlaylistEntity
import com.example.data.model.Song
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.GoldBrassPrimary
import com.example.ui.theme.MixeCrimson
import com.example.ui.theme.MixeTurquoise
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.viewmodel.MusicViewModel

@Composable
fun CatalogScreen(
    viewModel: MusicViewModel,
    searchQuery: String,
    activeGenre: String,
    favoriteIds: List<String>,
    playlists: List<PlaylistEntity>,
    currentSongId: String,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val genres = listOf("Todos", "Son y Jarabe", "Cumbia Mixe", "En Vivo / Calenda", "Danzón", "Chilena", "Marcha Festiva")
    var songForPlaylistDialog by remember { mutableStateOf<Song?>(null) }
    val allSongs by viewModel.allSongs.collectAsState()

    val filteredSongs = allSongs.filter { song ->
        val matchesGenre = activeGenre == "Todos" || song.genre.contains(activeGenre, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() ||
                song.title.contains(searchQuery, ignoreCase = true) ||
                song.artist.contains(searchQuery, ignoreCase = true) ||
                (song.ayuukTitle?.contains(searchQuery, ignoreCase = true) == true) ||
                song.community.contains(searchQuery, ignoreCase = true)
        matchesGenre && matchesSearch
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("catalog_screen_content")
    ) {
        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("search_text_field"),
            placeholder = { Text("Buscar son, cumbia, jarabe o banda...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = GoldBrassPrimary
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Limpiar búsqueda",
                            tint = TextSecondary
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DarkSurfaceCard,
                unfocusedContainerColor = DarkSurfaceCard,
                focusedBorderColor = GoldBrassPrimary,
                unfocusedBorderColor = DarkBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = GoldBrassPrimary
            )
        )

        // Genre Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            items(genres) { genre ->
                val selected = genre == activeGenre
                FilterChip(
                    selected = selected,
                    onClick = { viewModel.setActiveGenre(genre) },
                    label = {
                        Text(
                            text = genre,
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = DarkSurfaceCard,
                        labelColor = TextSecondary,
                        selectedContainerColor = GoldBrassPrimary,
                        selectedLabelColor = Color.Black
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selected,
                        borderColor = if (selected) GoldBrassPrimary else DarkBorder
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }

        // Results Summary
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filteredSongs.size} Grabaciones Disponibles",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )
            Text(
                text = "Audio Sintetizado en Vivo",
                style = MaterialTheme.typography.labelSmall,
                color = MixeTurquoise
            )
        }

        // List of Songs
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredSongs) { song ->
                val isCurrent = currentSongId == song.id
                SongRowItem(
                    song = song,
                    isCurrent = isCurrent,
                    isPlaying = isCurrent && isPlaying,
                    isFavorite = favoriteIds.contains(song.id),
                    onPlay = { viewModel.playSong(song) },
                    onToggleFavorite = { viewModel.toggleFavorite(song.id) },
                    onAddToPlaylist = { songForPlaylistDialog = song },
                    onInfo = { viewModel.selectSongForDetails(song) }
                )
            }
        }
    }

    // Add To Playlist Dialog
    if (songForPlaylistDialog != null) {
        val targetSong = songForPlaylistDialog!!
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { songForPlaylistDialog = null },
            title = {
                Text(
                    text = "Añadir a Lista de Reproducción",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Selecciona la lista para '${targetSong.title}':",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    playlists.forEach { playlist ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.addSongToPlaylist(playlist.id, targetSong.id)
                                    songForPlaylistDialog = null
                                },
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = playlist.name,
                                style = MaterialTheme.typography.titleSmall,
                                color = GoldBrassPrimary,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { songForPlaylistDialog = null }) {
                    Text("Cerrar", color = TextSecondary)
                }
            },
            containerColor = DarkSurfaceCard
        )
    }
}

@Composable
fun SongRowItem(
    song: Song,
    isCurrent: Boolean,
    isPlaying: Boolean,
    isFavorite: Boolean,
    onPlay: () -> Unit,
    onToggleFavorite: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onInfo: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPlay() }
            .testTag("catalog_song_row_${song.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent) DarkBorder else DarkSurfaceCard
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Album Art with Play Overlay
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Image(
                    painter = painterResource(id = song.coverResId),
                    contentDescription = song.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                if (isCurrent) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0x88000000)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = GoldBrassPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (isCurrent) GoldBrassPrimary else TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (song.ayuukTitle != null) {
                    Text(
                        text = "Ayuuk: ${song.ayuukTitle}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MixeTurquoise,
                        fontSize = 10.sp
                    )
                }
                Text(
                    text = "${song.artist} • ${formatDuration(song.durationSec)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Actions
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onInfo, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Detalles culturales",
                        tint = TextTertiary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(onClick = onAddToPlaylist, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Default.PlaylistAdd,
                        contentDescription = "Añadir a lista",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(onClick = onToggleFavorite, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (isFavorite) MixeCrimson else TextTertiary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

fun formatDuration(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "%d:%02d".format(m, s)
}
