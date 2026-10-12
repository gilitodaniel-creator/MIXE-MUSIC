package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.GraphicEq
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.EQ_BANDS_INFO
import com.example.audio.PROFESSIONAL_EQ_PRESETS
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.GoldBrassContainer
import com.example.ui.theme.GoldBrassPrimary
import com.example.ui.theme.GoldBrassSecondary
import com.example.ui.theme.MixeCrimson
import com.example.ui.theme.MixeTurquoise
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.viewmodel.MusicViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalEqualizerModal(
    viewModel: MusicViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val eqState by viewModel.equalizerState.collectAsState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkBackground,
        dragHandle = null,
        modifier = Modifier.testTag("professional_equalizer_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(GoldBrassPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Ecualizador",
                            tint = GoldBrassPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "ECUALIZADOR PRO",
                            style = MaterialTheme.typography.titleMedium,
                            color = GoldBrassPrimary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Calibración Sonora GD Music • 5 Bandas DSP",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Master ON/OFF Switch
                    Switch(
                        checked = eqState.isEnabled,
                        onCheckedChange = { viewModel.setEqualizerEnabled(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = GoldBrassPrimary,
                            uncheckedThumbColor = TextTertiary,
                            uncheckedTrackColor = DarkSurfaceCard
                        ),
                        modifier = Modifier.testTag("eq_master_switch")
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_eq_modal_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Status Badge
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (eqState.isEnabled) GoldBrassPrimary.copy(alpha = 0.15f) else DarkSurfaceCard,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (eqState.isEnabled) "PROCESAMIENTO DSP ACTIVO" else "MODO BYPASS (DESACTIVADO)",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (eqState.isEnabled) GoldBrassPrimary else TextTertiary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Preset: ${eqState.activePresetName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (eqState.isEnabled) Color.White else TextTertiary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Graphical EQ Curve Canvas Visualizer
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(DarkBorder, GoldBrassPrimary.copy(alpha = 0.4f), DarkBorder))
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Curva de Respuesta de Frecuencias",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary
                        )
                        Text(
                            text = "+12 dB / -12 dB",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiary,
                            fontSize = 10.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    EqualizerCurveVisualizer(
                        bandLevels = eqState.bandLevels,
                        isEnabled = eqState.isEnabled,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(95.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Presets Horizontal Carousel
            Text(
                text = "AJUSTES PREESTABLECIDOS (PRESETS)",
                style = MaterialTheme.typography.labelMedium,
                color = GoldBrassPrimary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PROFESSIONAL_EQ_PRESETS.forEach { preset ->
                    val isSelected = eqState.activePresetName == preset.name
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.applyEqualizerPreset(preset) },
                        label = {
                            Text(
                                text = preset.name,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
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
                            selected = isSelected,
                            borderColor = if (isSelected) GoldBrassPrimary else DarkBorder
                        ),
                        modifier = Modifier.testTag("eq_preset_${preset.name.replace(" ", "_").lowercase()}")
                    )
                }
            }

            // Description of active preset
            val currentPresetObj = PROFESSIONAL_EQ_PRESETS.find { it.name == eqState.activePresetName }
            if (currentPresetObj != null && currentPresetObj.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = currentPresetObj.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 5 Band Gain Sliders
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "BANDAS DE ECUALIZACIÓN",
                    style = MaterialTheme.typography.labelMedium,
                    color = GoldBrassPrimary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )

                IconButton(
                    onClick = { viewModel.resetEqualizerFlat() },
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("reset_flat_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = "Ajuste Plano",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                EQ_BANDS_INFO.forEach { bandInfo ->
                    val bandIndex = bandInfo.index
                    val currentDb = eqState.bandLevels.getOrElse(bandIndex) { 0f }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("eq_band_card_$bandIndex"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(DarkBorder, DarkBorder)))
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = GoldBrassContainer
                                    ) {
                                        Text(
                                            text = bandInfo.frequencyLabel,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = GoldBrassPrimary,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = bandInfo.instrumentFocus,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (currentDb > 0.1f) MixeCrimson.copy(alpha = 0.3f)
                                    else if (currentDb < -0.1f) MixeTurquoise.copy(alpha = 0.3f)
                                    else DarkSurface
                                ) {
                                    val sign = if (currentDb > 0f) "+" else ""
                                    Text(
                                        text = "$sign${String.format(Locale.US, "%.1f", currentDb)} dB",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (currentDb > 0.1f) GoldBrassPrimary else if (currentDb < -0.1f) MixeTurquoise else TextTertiary,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Slider(
                                value = currentDb,
                                onValueChange = { newVal ->
                                    viewModel.setEqualizerBandLevel(bandIndex, newVal)
                                },
                                valueRange = -12f..12f,
                                steps = 23,
                                colors = SliderDefaults.colors(
                                    thumbColor = GoldBrassPrimary,
                                    activeTrackColor = GoldBrassPrimary,
                                    inactiveTrackColor = DarkBorder
                                ),
                                enabled = eqState.isEnabled,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("eq_band_slider_$bandIndex")
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // DSP Boost & Spatial Section
            Text(
                text = "PROCESADORES DE AUDIO DSP",
                style = MaterialTheme.typography.labelMedium,
                color = GoldBrassPrimary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Bass Boost Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(DarkBorder, DarkBorder)))
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
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = null,
                                tint = GoldBrassPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Column {
                                Text(
                                    text = "Refuerzo de Graves Serranos (Bass Boost)",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Pegada profunda para sousáfono y bombo de fiesta",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GoldBrassContainer
                        ) {
                            Text(
                                text = "${(eqState.bassBoost * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldBrassPrimary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Slider(
                        value = eqState.bassBoost,
                        onValueChange = { viewModel.setEqualizerBassBoost(it) },
                        valueRange = 0f..1f,
                        enabled = eqState.isEnabled,
                        colors = SliderDefaults.colors(
                            thumbColor = GoldBrassPrimary,
                            activeTrackColor = GoldBrassPrimary,
                            inactiveTrackColor = DarkBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("eq_bass_boost_slider")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3D Surround Spatial Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(DarkBorder, DarkBorder)))
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
                            Icon(
                                imageVector = Icons.Default.SurroundSound,
                                contentDescription = null,
                                tint = MixeTurquoise,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = "Espacio Acústico 3D (Calenda Surround)",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Inmersión atmosférica de calenda en explanada",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MixeTurquoise.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "${(eqState.surround3D * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = MixeTurquoise,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Slider(
                        value = eqState.surround3D,
                        onValueChange = { viewModel.setEqualizerSurround3D(it) },
                        valueRange = 0f..1f,
                        enabled = eqState.isEnabled,
                        colors = SliderDefaults.colors(
                            thumbColor = MixeTurquoise,
                            activeTrackColor = MixeTurquoise,
                            inactiveTrackColor = DarkBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("eq_surround_3d_slider")
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Close Button
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldBrassPrimary,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("apply_and_close_eq_button")
            ) {
                Text(
                    text = "Aplicar Ajustes y Cerrar",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun EqualizerCurveVisualizer(
    bandLevels: List<Float>,
    isEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val centerY = height / 2f

        // Draw 0dB center line
        drawLine(
            color = Color.White.copy(alpha = 0.15f),
            start = Offset(0f, centerY),
            end = Offset(width, centerY),
            strokeWidth = 1.5f
        )

        // Draw +6dB, -6dB guide lines
        val plus6Y = centerY - (6f / 12f) * (height / 2f) * 0.85f
        val minus6Y = centerY + (6f / 12f) * (height / 2f) * 0.85f
        drawLine(
            color = Color.White.copy(alpha = 0.05f),
            start = Offset(0f, plus6Y),
            end = Offset(width, plus6Y),
            strokeWidth = 1f
        )
        drawLine(
            color = Color.White.copy(alpha = 0.05f),
            start = Offset(0f, minus6Y),
            end = Offset(width, minus6Y),
            strokeWidth = 1f
        )

        if (bandLevels.isEmpty()) return@Canvas

        val numBands = bandLevels.size
        val points = mutableListOf<Offset>()

        for (i in 0 until numBands) {
            val levelDb = if (isEnabled) bandLevels[i] else 0f
            val x = (width / (numBands + 1)) * (i + 1)
            // levelDb between -12 and +12
            val y = centerY - (levelDb / 12f) * (height / 2f) * 0.85f
            points.add(Offset(x, y.coerceIn(4f, height - 4f)))
        }

        // Draw smooth path
        val path = Path()
        val fillPath = Path()

        path.moveTo(0f, centerY)
        fillPath.moveTo(0f, centerY)

        for (i in 0 until points.size) {
            val current = points[i]
            val prev = if (i > 0) points[i - 1] else Offset(0f, centerY)
            val cX = (prev.x + current.x) / 2f
            path.cubicTo(cX, prev.y, cX, current.y, current.x, current.y)
            fillPath.cubicTo(cX, prev.y, cX, current.y, current.x, current.y)
        }

        val lastPoint = points.last()
        path.cubicTo((lastPoint.x + width) / 2f, lastPoint.y, (lastPoint.x + width) / 2f, centerY, width, centerY)
        fillPath.cubicTo((lastPoint.x + width) / 2f, lastPoint.y, (lastPoint.x + width) / 2f, centerY, width, centerY)

        fillPath.lineTo(width, height)
        fillPath.lineTo(0f, height)
        fillPath.close()

        // Draw gradient fill under the curve
        val gradientBrush = Brush.verticalGradient(
            colors = listOf(
                if (isEnabled) GoldBrassPrimary.copy(alpha = 0.35f) else Color.Gray.copy(alpha = 0.15f),
                Color.Transparent
            ),
            startY = 0f,
            endY = height
        )
        drawPath(path = fillPath, brush = gradientBrush)

        // Draw curve stroke
        drawPath(
            path = path,
            color = if (isEnabled) GoldBrassPrimary else Color.Gray.copy(alpha = 0.6f),
            style = Stroke(width = 3.dp.toPx())
        )

        // Draw anchor points
        points.forEach { pt ->
            drawCircle(
                color = if (isEnabled) GoldBrassSecondary else Color.LightGray,
                radius = 5.dp.toPx(),
                center = pt
            )
            drawCircle(
                color = Color.Black,
                radius = 2.dp.toPx(),
                center = pt
            )
        }
    }
}
