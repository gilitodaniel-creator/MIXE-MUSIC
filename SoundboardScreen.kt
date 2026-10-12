package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.BrassInstrument
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class InstrumentItem(
    val instrument: BrassInstrument,
    val icon: ImageVector,
    val accentColor: Color,
    val noteLabel: String
)

@Composable
fun SoundboardScreen(
    viewModel: MusicViewModel,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var activeInstrumentId by remember { mutableStateOf<String?>(null) }

    val instrumentList = listOf(
        InstrumentItem(
            BrassInstrument.TROMPETA,
            Icons.Default.Campaign,
            GoldBrassPrimary,
            "Bb4 • Voz Principal"
        ),
        InstrumentItem(
            BrassInstrument.CLARINETE,
            Icons.Default.MusicNote,
            MixeTurquoise,
            "F4 • Floreo y Trino"
        ),
        InstrumentItem(
            BrassInstrument.TROMBON,
            Icons.Default.SurroundSound,
            GoldBrassSecondary,
            "Bb3 • Armonía Potente"
        ),
        InstrumentItem(
            BrassInstrument.SOUSAFONO,
            Icons.Default.Radio,
            MixeCrimson,
            "Bb2 • Tumbao Bajo"
        ),
        InstrumentItem(
            BrassInstrument.BOMBO,
            Icons.Default.GraphicEq,
            Color(0xFFFF9800),
            "C2 • Pulso Calenda"
        ),
        InstrumentItem(
            BrassInstrument.PLATILLOS,
            Icons.Default.Campaign,
            Color(0xFFFFD54F),
            "High • Brillo Festivo"
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("soundboard_screen_content")
    ) {
        // Header
        Column(modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)) {
            Text(
                text = "Estudio & Metales en Vivo",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
            Text(
                text = "Toca los instrumentos de la Banda Filarmónica de la Sierra Mixe y acompaña el ritmo.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        // Pro Equalizer Quick Bar
        val eqState by viewModel.equalizerState.collectAsState()
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("soundboard_equalizer_banner"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.horizontalGradient(listOf(DarkBorder, GoldBrassPrimary.copy(alpha = 0.5f)))
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
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
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = GoldBrassPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Ecualizador Pro GD",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (eqState.isEnabled) "Preset: ${eqState.activePresetName}" else "Bypass Desactivado",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (eqState.isEnabled) GoldBrassPrimary else TextTertiary,
                            fontSize = 11.sp
                        )
                    }
                }

                Button(
                    onClick = { viewModel.setShowEqualizerDialog(true) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldBrassPrimary,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("soundboard_open_eq_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Calibrar EQ",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Instructions Tip Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(DarkBorder, DarkBorder)))
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = GoldBrassPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Generación de síntesis acústica en tiempo real por el motor de GD Music. ¡Pruébalo con la música de fondo sonando!",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Instruments Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(bottom = 120.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(instrumentList) { item ->
                val isPulsing = activeInstrumentId == item.instrument.name
                val scale by animateFloatAsState(
                    targetValue = if (isPulsing) 0.94f else 1.0f,
                    animationSpec = tween(durationMillis = 100),
                    label = "pad_scale"
                )

                Card(
                    modifier = Modifier
                        .scale(scale)
                        .clickable {
                            activeInstrumentId = item.instrument.name
                            viewModel.playSingleInstrument(item.instrument)
                            scope.launch {
                                delay(220)
                                if (activeInstrumentId == item.instrument.name) {
                                    activeInstrumentId = null
                                }
                            }
                        }
                        .testTag("instrument_pad_${item.instrument.name.lowercase()}"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isPulsing) item.accentColor.copy(alpha = 0.25f) else DarkSurfaceCard
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(
                            if (isPulsing) listOf(item.accentColor, item.accentColor) else listOf(DarkBorder, DarkBorder)
                        )
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(item.accentColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.instrument.displayName,
                                tint = item.accentColor,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Text(
                            text = item.instrument.displayName,
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DarkSurface
                        ) {
                            Text(
                                text = item.noteLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = item.accentColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 10.sp
                            )
                        }

                        Text(
                            text = item.instrument.timbre,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
