package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.GoldBrassPrimary
import com.example.ui.theme.MixeCrimson
import com.example.ui.theme.MixeTurquoise
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MusicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishSongScreen(
    viewModel: MusicViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var ayuukTitle by remember { mutableStateOf("") }
    var artist by remember { mutableStateOf("") }
    var community by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("Son y Jarabe") }
    var melodyTypeStr by remember { mutableStateOf("JARABE_MIXE") }
    var musicalKey by remember { mutableStateOf("Bb Mayor") }
    var bpmSlider by remember { mutableFloatStateOf(120f) }
    var durationSlider by remember { mutableFloatStateOf(210f) }
    var culturalNotes by remember { mutableStateOf("") }
    var audioUrl by remember { mutableStateOf("") }

    val isPublishing = viewModel.isPublishing.value
    val publishSuccess = viewModel.publishSuccessMessage.value
    val publishError = viewModel.publishErrorMessage.value

    val genres = listOf("Son y Jarabe", "Cumbia Mixe", "En Vivo / Calenda", "Danzón", "Chilena", "Marcha Festiva")
    val melodyTypes = listOf(
        "JARABE_MIXE" to "Jarabe Mixe (Tradicional)",
        "SON_CALENDA" to "Son de Calenda (Marcha)",
        "CUMBIA_TROPICAL" to "Cumbia Mixe (Sabor)",
        "DANZON_GALA" to "Danzón (Elegante)",
        "MARCHA_FESTIVA" to "Marcha de Fiesta",
        "CHILENA_OAXACA" to "Chilena Mixe"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("publish_song_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Bar
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Regresar",
                        tint = TextPrimary
                    )
                }

                Column {
                    Text(
                        text = "Publicar en GD Music Nube",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary
                    )
                    Text(
                        text = "Tu música estará disponible de inmediato para todos los oyentes.",
                        style = MaterialTheme.typography.bodySmall,
                        color = GoldBrassPrimary
                    )
                }
            }
        }

        // Quick Auto-fill button for testing
        item {
            OutlinedButton(
                onClick = {
                    title = "Son de Bienvenida Mixe 2026"
                    ayuukTitle = "Mäjtën Tääk Xäjpët"
                    artist = "Banda Filarmónica de Santa María Tlahuitoltepec"
                    community = "Santa María Tlahuitoltepec, Mixe"
                    genre = "Son y Jarabe"
                    melodyTypeStr = "JARABE_MIXE"
                    musicalKey = "Bb Mayor"
                    bpmSlider = 124f
                    durationSlider = 240f
                    culturalNotes = "Grabado en vivo en la fiesta patronal por GD Music Producciones. Representa la hermandad y bienvenida de las bandas filarmónicas en la serranía mixe."
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = GoldBrassPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Llenar con Ejemplo de la Sierra",
                    style = MaterialTheme.typography.labelMedium,
                    color = GoldBrassPrimary
                )
            }
        }

        // Form Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(DarkBorder, DarkBorder)))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "1. Datos de la Obra Musical",
                        style = MaterialTheme.typography.titleSmall,
                        color = GoldBrassPrimary
                    )

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Título de la Canción *") },
                        placeholder = { Text("ej. Jarabe del Zempoaltépetl") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("publish_title_input"),
                        colors = formFieldColors()
                    )

                    OutlinedTextField(
                        value = ayuukTitle,
                        onValueChange = { ayuukTitle = it },
                        label = { Text("Título en Lengua Ayuuk (opcional)") },
                        placeholder = { Text("ej. Ipxukp Jä’äy") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("publish_ayuuk_title_input"),
                        colors = formFieldColors()
                    )

                    OutlinedTextField(
                        value = artist,
                        onValueChange = { artist = it },
                        label = { Text("Banda Filarmónica o Intérprete *") },
                        placeholder = { Text("ej. Banda Filarmónica de Ayutla") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("publish_artist_input"),
                        colors = formFieldColors()
                    )

                    OutlinedTextField(
                        value = community,
                        onValueChange = { community = it },
                        label = { Text("Comunidad de Origen *") },
                        placeholder = { Text("ej. San Pedro y San Pablo Ayutla") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("publish_community_input"),
                        colors = formFieldColors()
                    )
                }
            }
        }

        // Genre and Sound synthesis
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(DarkBorder, DarkBorder)))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "2. Género y Motor de Audio",
                        style = MaterialTheme.typography.titleSmall,
                        color = GoldBrassPrimary
                    )

                    Text(
                        text = "Selecciona el género:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        genres.take(3).forEach { g ->
                            val selected = genre == g
                            FilterChip(
                                selected = selected,
                                onClick = { genre = g },
                                label = { Text(g, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = DarkSurface,
                                    labelColor = TextSecondary,
                                    selectedContainerColor = GoldBrassPrimary,
                                    selectedLabelColor = Color.Black
                                )
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        genres.drop(3).forEach { g ->
                            val selected = genre == g
                            FilterChip(
                                selected = selected,
                                onClick = { genre = g },
                                label = { Text(g, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = DarkSurface,
                                    labelColor = TextSecondary,
                                    selectedContainerColor = GoldBrassPrimary,
                                    selectedLabelColor = Color.Black
                                )
                            )
                        }
                    }

                    Text(
                        text = "Patrón Melódico Acústico (para reproducción en vivo):",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    melodyTypes.forEach { (typeKey, typeLabel) ->
                        val selected = melodyTypeStr == typeKey
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    1.dp,
                                    if (selected) GoldBrassPrimary else DarkBorder,
                                    RoundedCornerShape(8.dp)
                                ),
                            color = if (selected) GoldBrassPrimary.copy(alpha = 0.15f) else DarkSurface
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = typeLabel,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (selected) GoldBrassPrimary else TextPrimary
                                )
                                OutlinedButton(
                                    onClick = { melodyTypeStr = typeKey },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = if (selected) GoldBrassPrimary else TextSecondary
                                    )
                                ) {
                                    Text(if (selected) "Seleccionado" else "Elegir", fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    // Tempo BPM
                    Column {
                        Text(
                            text = "Tempo: ${bpmSlider.toInt()} BPM",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary
                        )
                        Slider(
                            value = bpmSlider,
                            onValueChange = { bpmSlider = it },
                            valueRange = 70f..160f,
                            colors = SliderDefaults.colors(thumbColor = GoldBrassPrimary, activeTrackColor = GoldBrassPrimary)
                        )
                    }

                    // Duración
                    Column {
                        val min = durationSlider.toInt() / 60
                        val sec = durationSlider.toInt() % 60
                        Text(
                            text = "Duración estimada: %d:%02d".format(min, sec),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary
                        )
                        Slider(
                            value = durationSlider,
                            onValueChange = { durationSlider = it },
                            valueRange = 60f..400f,
                            colors = SliderDefaults.colors(thumbColor = GoldBrassPrimary, activeTrackColor = GoldBrassPrimary)
                        )
                    }
                }
            }
        }

        // Cultural notes & external audio
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(DarkBorder, DarkBorder)))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "3. Significado Cultural & Notas de Fiesta",
                        style = MaterialTheme.typography.titleSmall,
                        color = GoldBrassPrimary
                    )

                    OutlinedTextField(
                        value = culturalNotes,
                        onValueChange = { culturalNotes = it },
                        label = { Text("Historia de la pieza o fiesta patronal *") },
                        placeholder = { Text("Describe el significado tradicional, mayordomía o festejo en la comunidad...") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth().testTag("publish_notes_input"),
                        colors = formFieldColors()
                    )

                    OutlinedTextField(
                        value = audioUrl,
                        onValueChange = { audioUrl = it },
                        label = { Text("URL de Audio Stream (opcional)") },
                        placeholder = { Text("https://ejemplo.com/audio.mp3") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("publish_audio_url_input"),
                        colors = formFieldColors()
                    )
                }
            }
        }

        // Submit Button & Messages
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = {
                        viewModel.publishNewSong(
                            title = title,
                            ayuukTitle = ayuukTitle,
                            artist = artist,
                            community = community,
                            genre = genre,
                            melodyTypeStr = melodyTypeStr,
                            durationSec = durationSlider.toInt(),
                            bpm = bpmSlider.toInt(),
                            musicalKey = musicalKey,
                            culturalNotes = culturalNotes,
                            audioUrl = if (audioUrl.isNotBlank()) audioUrl else null
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_publish_song_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldBrassPrimary,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(14.dp),
                    enabled = !isPublishing
                ) {
                    if (isPublishing) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Publicar en GD Music Nube",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                if (publishSuccess != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GoldBrassPrimary.copy(alpha = 0.2f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = GoldBrassPrimary
                            )
                            Text(
                                text = publishSuccess,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary
                            )
                        }
                    }
                }

                if (publishError != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MixeCrimson.copy(alpha = 0.2f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = publishError,
                            style = MaterialTheme.typography.bodySmall,
                            color = MixeCrimson,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun formFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = GoldBrassPrimary,
    unfocusedBorderColor = DarkBorder,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedContainerColor = DarkSurface,
    unfocusedContainerColor = DarkSurface
)
