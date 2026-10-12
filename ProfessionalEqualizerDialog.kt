package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.material.icons.filled.VolumeUp
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.EqualizerPreset
import com.example.audio.EqualizerState
import com.example.audio.PROFESSIONAL_EQ_PRESETS
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

val BAND_LABELS = listOf(
    "60 Hz" to "Sub-Bajo",
    "230 Hz" to "Tuba",
    "910 Hz" to "Medios",
    "3.6 kHz" to "Clarinete",
    "14 kHz" to "Platillos"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalEqualizerDialog(
    equalizerState: EqualizerState,
    onEqualizerChange: (EqualizerState) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkBackground,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .testTag("professional_equalizer_sheet"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with On/Off Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(GoldBrassPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Equalizer,
                            contentDescription = null,
                            tint = GoldBrassPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Ecualizador Profesional",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Calibración de Audio GD Music",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldBrassPrimary
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (equalizerState.isEnabled) "ACTIVO" else "BYPASS",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (equalizerState.isEnabled) GoldBrassPrimary else TextTertiary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = equalizerState.isEnabled,
                        onCheckedChange = { onEqualizerChange(equalizerState.copy(isEnabled = it)) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = GoldBrassPrimary,
                            checkedTrackColor = DarkSurfaceCard,
                            uncheckedThumbColor = TextTertiary,
                            uncheckedTrackColor = DarkSurface
                        ),
                        modifier = Modifier.testTag("eq_bypass_switch")
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = TextSecondary
                        )
                    }
                }
            }

            // Frequency Response Curve Canvas (Visual Spectrum Graph)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(DarkBorder, DarkBorder)))
            ) {
                Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val midY = h / 2f

                        // Draw Grid lines
                        drawLine(
                            color = DarkBorder,
                            start = Offset(0f, midY),
                            end = Offset(w, midY),
                            strokeWidth = 1f
                        )
                        drawLine(
                            color = DarkBorder.copy(alpha = 0.5f),
                            start = Offset(0f, h * 0.25f),
                            end = Offset(w, h * 0.25f),
                            strokeWidth = 1f
                        )
                        drawLine(
                            color = DarkBorder.copy(alpha = 0.5f),
                            start = Offset(0f, h * 0.75f),
                            end = Offset(w, h * 0.75f),
                            strokeWidth = 1f
                        )

                        // Draw frequency curve
                        val bands = equalizerState.bandLevels
                        val path = Path()
                        val stepX = w / (bands.size - 1)

                        bands.forEachIndexed { i, level ->
                            val normalizedY = if (equalizerState.isEnabled) {
                                midY - (level / 12f) * (midY * 0.85f)
                            } else {
                                midY
                            }
                            val x = i * stepX
                            if (i == 0) path.moveTo(x, normalizedY)
                            else {
                                val prevX = (i - 1) * stepX
                                val prevLevel = bands[i - 1]
                                val prevY = if (equalizerState.isEnabled) {
                                    midY - (prevLevel / 12f) * (midY * 0.85f)
                                } else midY
                                val cX1 = prevX + stepX / 2f
                                val cX2 = prevX + stepX / 2f
                                path.cubicTo(cX1, prevY, cX2, normalizedY, x, normalizedY)
                            }
                        }

                        drawPath(
                            path = path,
                            brush = Brush.horizontalGradient(
                                listOf(MixeCrimson, GoldBrassPrimary, MixeTurquoise)
                            ),
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Bajos (60Hz)", color = TextTertiary, fontSize = 9.sp)
                        Text("Medios (1kHz)", color = TextTertiary, fontSize = 9.sp)
                        Text("Agudos (14kHz)", color = TextTertiary, fontSize = 9.sp)
                    }
                }
            }

            // Presets Selector Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(PROFESSIONAL_EQ_PRESETS) { preset ->
                    val isSelected = equalizerState.activePresetName == preset.name
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            onEqualizerChange(
                                equalizerState.copy(
                                    activePresetName = preset.name,
                                    bandLevels = preset.bandLevels,
                                    bassBoost = preset.bassBoost,
                                    surround3D = preset.surround3D
                                )
                            )
                        },
                        label = {
                            Text(
                                text = preset.name,
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
                            selected = isSelected,
                            borderColor = if (isSelected) GoldBrassPrimary else DarkBorder
                        )
                    )
                }
            }

            // 5 Graphic Equalizer Faders
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(DarkBorder, DarkBorder)))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Bandas Paramétricas (Ganancia en dB)",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldBrassPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        equalizerState.bandLevels.forEachIndexed { index, level ->
                            val labelInfo = BAND_LABELS[index]
                            BandFader(
                                freq = labelInfo.first,
                                desc = labelInfo.second,
                                levelDb = level,
                                isEnabled = equalizerState.isEnabled,
                                onLevelChange = { newDb ->
                                    val newLevels = equalizerState.bandLevels.toMutableList()
                                    newLevels[index] = newDb
                                    onEqualizerChange(
                                        equalizerState.copy(
                                            bandLevels = newLevels,
                                            activePresetName = "Personalizado"
                                        )
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Bass Boost & Surround 3D Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Bass Boost Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(DarkBorder, DarkBorder)))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = null,
                                tint = MixeCrimson,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "BASS BOOST",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "${(equalizerState.bassBoost * 100).toInt()}% (Bombo Mixe)",
                            style = MaterialTheme.typography.bodySmall,
                            color = GoldBrassPrimary,
                            fontSize = 11.sp
                        )

                        Slider(
                            value = equalizerState.bassBoost,
                            onValueChange = {
                                onEqualizerChange(
                                    equalizerState.copy(
                                        bassBoost = it,
                                        activePresetName = "Personalizado"
                                    )
                                )
                            },
                            valueRange = 0f..1f,
                            enabled = equalizerState.isEnabled,
                            colors = SliderDefaults.colors(
                                thumbColor = MixeCrimson,
                                activeTrackColor = MixeCrimson,
                                inactiveTrackColor = DarkBorder
                            )
                        )
                    }
                }

                // Surround 3D / Espacio Atrio Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(DarkBorder, DarkBorder)))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SurroundSound,
                                contentDescription = null,
                                tint = MixeTurquoise,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "ESPACIO 3D",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "${(equalizerState.surround3D * 100).toInt()}% (Atrio Festivo)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MixeTurquoise,
                            fontSize = 11.sp
                        )

                        Slider(
                            value = equalizerState.surround3D,
                            onValueChange = {
                                onEqualizerChange(
                                    equalizerState.copy(
                                        surround3D = it,
                                        activePresetName = "Personalizado"
                                    )
                                )
                            },
                            valueRange = 0f..1f,
                            enabled = equalizerState.isEnabled,
                            colors = SliderDefaults.colors(
                                thumbColor = MixeTurquoise,
                                activeTrackColor = MixeTurquoise,
                                inactiveTrackColor = DarkBorder
                            )
                        )
                    }
                }
            }

            // Bottom Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        val flat = PROFESSIONAL_EQ_PRESETS.last()
                        onEqualizerChange(
                            equalizerState.copy(
                                activePresetName = flat.name,
                                bandLevels = flat.bandLevels,
                                bassBoost = flat.bassBoost,
                                surround3D = flat.surround3D
                            )
                        )
                    },
                    modifier = Modifier.weight(1f).height(44.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkSurfaceCard,
                        contentColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Restablecer", style = MaterialTheme.typography.labelSmall)
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1.5f).height(44.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldBrassPrimary,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Aplicar Ajustes",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun BandFader(
    freq: String,
    desc: String,
    levelDb: Float,
    isEnabled: Boolean,
    onLevelChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val sign = if (levelDb > 0) "+" else ""
        Text(
            text = "$sign${levelDb.toInt()} dB",
            style = MaterialTheme.typography.labelSmall,
            color = if (isEnabled && levelDb != 0f) GoldBrassPrimary else TextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )

        // Vertical Slider simulated with small styled slider
        Slider(
            value = levelDb,
            onValueChange = onLevelChange,
            valueRange = -12f..12f,
            enabled = isEnabled,
            colors = SliderDefaults.colors(
                thumbColor = if (levelDb > 0) GoldBrassPrimary else TextTertiary,
                activeTrackColor = GoldBrassPrimary,
                inactiveTrackColor = DarkBorder
            ),
            modifier = Modifier.height(32.dp)
        )

        Text(
            text = freq,
            style = MaterialTheme.typography.labelSmall,
            color = TextPrimary,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = desc,
            style = MaterialTheme.typography.bodySmall,
            color = TextTertiary,
            fontSize = 9.sp,
            textAlign = TextAlign.Center
        )
    }
}
