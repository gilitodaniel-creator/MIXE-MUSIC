package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.GoldBrassPrimary
import com.example.ui.theme.MixeCrimson

@Composable
fun WaveformVisualizer(
    amplitudes: List<Float>,
    modifier: Modifier = Modifier,
    isPlaying: Boolean = true,
    maxHeight: Dp = 48.dp,
    barWidth: Dp = 4.dp,
    barSpacing: Dp = 3.dp,
    color: Color = GoldBrassPrimary
) {
    Row(
        modifier = modifier
            .testTag("waveform_visualizer")
            .height(maxHeight),
        horizontalArrangement = Arrangement.spacedBy(barSpacing, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        amplitudes.forEachIndexed { index, amp ->
            val animatedAmp by animateFloatAsState(
                targetValue = if (isPlaying) amp.coerceIn(0.1f, 1.0f) else 0.15f,
                animationSpec = tween(durationMillis = 90),
                label = "bar_$index"
            )

            val barHeight = maxHeight * animatedAmp
            val barColor = if (index % 4 == 0) MixeCrimson else color

            Box(
                modifier = Modifier
                    .width(barWidth)
                    .height(barHeight.coerceAtLeast(4.dp))
                    .clip(RoundedCornerShape(percent = 50))
                    .background(barColor)
            )
        }
    }
}
