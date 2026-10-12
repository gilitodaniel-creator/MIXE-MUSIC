package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.WaveformVisualizer
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.GoldBrassPrimary
import com.example.ui.theme.GoldBrassSecondary
import com.example.ui.theme.MixeCrimson
import com.example.ui.theme.MixeTurquoise
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.viewmodel.MusicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    viewModel: MusicViewModel,
    isFavorite: Boolean,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val playerState = viewModel.playerState.value
    val song = playerState.currentSong

    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "vinyl_rotate"
    )

    val currentRotation = if (playerState.isPlaying) rotation else 0f
    var sliderPosition by remember(playerState.currentPositionSec) {
        mutableFloatStateOf(playerState.currentPositionSec.toFloat())
    }

    val equalizerState by viewModel.equalizerState.collectAsState()
    val speeds = listOf(0.8f, 1.0f, 1.2f)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("full_player_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onClose,
                modifier = Modifier.testTag("player_minimize_button")
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Minimizar",
                    tint = TextPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "REPRODUCIENDO DE",
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldBrassPrimary,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp
                )
                Text(
                    text = song.album,
                    style = MaterialTheme.typography.labelMedium,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = { viewModel.toggleFavorite(song.id) },
                modifier = Modifier.testTag("player_favorite_button")
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (isFavorite) MixeCrimson else TextPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Big Vinyl Disc / Cover Art
        Box(
            modifier = Modifier
                .size(260.dp)
                .shadow(24.dp, CircleShape, spotColor = GoldBrassPrimary)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF221A13),
                            Color(0xFF140F0A),
                            Color(0xFF000000)
                        )
                    )
                )
                .border(4.dp, GoldBrassPrimary.copy(alpha = 0.8f), CircleShape)
                .rotate(currentRotation),
            contentAlignment = Alignment.Center
        ) {
            // Grooves
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .border(1.dp, Color(0x33FFFFFF), CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .border(1.dp, Color(0x22FFFFFF), CircleShape)
            )

            // Center Album Art
            Image(
                painter = painterResource(id = song.coverResId),
                contentDescription = song.title,
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .border(2.dp, GoldBrassSecondary, CircleShape),
                contentScale = ContentScale.Crop
            )

            // Center spindle hole
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color.Black)
                    .border(2.dp, GoldBrassPrimary, CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Song Title, Native Ayuuk Title, Banda and Community
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.displayMedium,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                fontSize = 22.sp
            )
            if (song.ayuukTitle != null) {
                Text(
                    text = "Ayuuk: ${song.ayuukTitle}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MixeTurquoise,
                    fontSize = 14.sp
                )
            }
            Text(
                text = song.artist,
                style = MaterialTheme.typography.bodyLarge,
                color = GoldBrassPrimary,
                textAlign = TextAlign.Center
            )
            Text(
                text = "${song.community} • ${song.musicalKey} • ${song.bpm} BPM",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Waveform Visualizer
        WaveformVisualizer(
            amplitudes = playerState.visualizerBars,
            isPlaying = playerState.isPlaying,
            maxHeight = 44.dp,
            barWidth = 5.dp,
            barSpacing = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Progress Slider & Timestamps
        Column(modifier = Modifier.fillMaxWidth()) {
            Slider(
                value = sliderPosition,
                onValueChange = { sliderPosition = it },
                onValueChangeFinished = {
                    viewModel.seekTo(sliderPosition.toInt())
                },
                valueRange = 0f..song.durationSec.toFloat().coerceAtLeast(1f),
                colors = SliderDefaults.colors(
                    thumbColor = GoldBrassPrimary,
                    activeTrackColor = GoldBrassPrimary,
                    inactiveTrackColor = DarkBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("player_seek_slider")
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatDuration(playerState.currentPositionSec),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Text(
                    text = formatDuration(song.durationSec),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Player Main Controls Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.toggleShuffle() },
                modifier = Modifier.testTag("player_shuffle_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Shuffle,
                    contentDescription = "Modo aleatorio",
                    tint = if (playerState.isShuffle) GoldBrassPrimary else TextTertiary,
                    modifier = Modifier.size(24.dp)
                )
            }

            IconButton(
                onClick = { viewModel.playPrevious() },
                modifier = Modifier.size(48.dp).testTag("player_prev_button")
            ) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Anterior",
                    tint = TextPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Big Central Golden Play Button
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(GoldBrassPrimary, GoldBrassSecondary)
                        )
                    )
                    .clickable { viewModel.togglePlayPause() }
                    .testTag("player_main_play_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (playerState.isPlaying) "Pausar" else "Reproducir",
                    tint = DarkBackground,
                    modifier = Modifier.size(38.dp)
                )
            }

            IconButton(
                onClick = { viewModel.playNext() },
                modifier = Modifier.size(48.dp).testTag("player_next_button")
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Siguiente",
                    tint = TextPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }

            IconButton(
                onClick = { viewModel.toggleRepeat() },
                modifier = Modifier.testTag("player_repeat_button")
            ) {
                Icon(
                    imageVector = if (playerState.isRepeat) Icons.Default.RepeatOne else Icons.Default.Repeat,
                    contentDescription = "Repetir",
                    tint = if (playerState.isRepeat) GoldBrassPrimary else TextTertiary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Playback Speed Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Speed,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Velocidad de Ensayo: ",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            speeds.forEach { spd ->
                val selected = playerState.playbackSpeed == spd
                Surface(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { viewModel.setPlaybackSpeed(spd) },
                    color = if (selected) GoldBrassPrimary else DarkSurfaceCard,
                    contentColor = if (selected) Color.Black else TextPrimary
                ) {
                    Text(
                        text = "${spd}x",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Professional Equalizer Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("pro_equalizer_player_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.horizontalGradient(listOf(DarkBorder, GoldBrassPrimary.copy(alpha = 0.5f), DarkBorder))
            )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(GoldBrassPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = GoldBrassPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Ecualizador Profesional GD",
                                style = MaterialTheme.typography.titleSmall,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "5 Bandas • Bass Boost • Surround 3D",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (equalizerState.isEnabled) GoldBrassPrimary.copy(alpha = 0.2f) else DarkSurface
                    ) {
                        Text(
                            text = if (equalizerState.isEnabled) equalizerState.activePresetName else "BYPASS",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (equalizerState.isEnabled) GoldBrassPrimary else TextTertiary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Presets Selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    com.example.audio.PROFESSIONAL_EQ_PRESETS.forEach { preset ->
                        val selected = equalizerState.activePresetName == preset.name
                        FilterChip(
                            selected = selected,
                            onClick = { viewModel.applyEqualizerPreset(preset) },
                            label = {
                                Text(
                                    text = preset.name,
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = DarkSurface,
                                labelColor = TextSecondary,
                                selectedContainerColor = GoldBrassPrimary,
                                selectedLabelColor = Color.Black
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selected,
                                borderColor = if (selected) GoldBrassPrimary else DarkBorder
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Button to open full Equalizer modal console
                Button(
                    onClick = { viewModel.setShowEqualizerDialog(true) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkSurface,
                        contentColor = GoldBrassPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(DarkBorder, GoldBrassPrimary))),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .testTag("open_pro_equalizer_console_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = GoldBrassPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Ajustar Consola Pro (Frecuencias y DSP)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Cultural Meaning Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(DarkBorder, DarkBorder)))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Significado y Raíz Cultural Mixe",
                    style = MaterialTheme.typography.titleSmall,
                    color = GoldBrassPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = song.culturalNotes,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
