package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.example.data.model.MelodyType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

enum class BrassInstrument(val displayName: String, val noteFreq: Double, val timbre: String) {
    TROMPETA("Trompeta Mixe", 466.16, "Brillante y festiva"), // Bb4
    CLARINETE("Clarinete Ayuuk", 349.23, "Melodioso y lírico"), // F4
    TROMBON("Trombón de Vara", 233.08, "Potente y armónico"), // Bb3
    SOUSAFONO("Sousáfono / Tuba", 116.54, "Bajo profundo serrano"), // Bb2
    BOMBO("Bombo de Calenda", 65.41, "Golpe de fiesta patronal"), // C2
    PLATILLOS("Platillos de Bronce", 1200.0, "Choque brillante de calenda")
}

class MixeAudioEngine(private val scope: CoroutineScope) {

    private val sampleRate = 22050
    private var audioTrack: AudioTrack? = null
    private var playJob: Job? = null
    private var visualizerJob: Job? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPositionSec = MutableStateFlow(0)
    val currentPositionSec: StateFlow<Int> = _currentPositionSec.asStateFlow()

    private val _visualizerAmplitudes = MutableStateFlow(List(16) { 0.2f })
    val visualizerAmplitudes: StateFlow<List<Float>> = _visualizerAmplitudes.asStateFlow()

    private var activeMelodyType = MelodyType.JARABE_MIXE
    private var songDurationSec = 254
    private var playbackSpeedMultiplier = 1.0f
    private var equalizerState = EqualizerState()

    init {
        initAudioTrack()
    }

    private fun initAudioTrack() {
        try {
            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferSize = minBufferSize.coerceAtLeast(sampleRate * 2)

            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()
        } catch (_: Exception) {
            audioTrack = null
        }
    }

    fun playSong(melodyType: MelodyType, durationSec: Int, startPositionSec: Int = 0) {
        activeMelodyType = melodyType
        songDurationSec = durationSec
        _currentPositionSec.value = startPositionSec
        _isPlaying.value = true

        startSynthesisLoop()
        startVisualizerLoop()
    }

    fun pause() {
        _isPlaying.value = false
        playJob?.cancel()
        visualizerJob?.cancel()
        try {
            audioTrack?.pause()
            audioTrack?.flush()
        } catch (_: Exception) {}
        _visualizerAmplitudes.value = List(16) { 0.1f }
    }

    fun resume() {
        if (!_isPlaying.value) {
            _isPlaying.value = true
            startSynthesisLoop()
            startVisualizerLoop()
        }
    }

    fun seekTo(seconds: Int) {
        _currentPositionSec.value = seconds.coerceIn(0, songDurationSec)
    }

    fun setPlaybackSpeed(speed: Float) {
        playbackSpeedMultiplier = speed
    }

    fun updateEqualizer(state: EqualizerState) {
        equalizerState = state
    }

    private fun startSynthesisLoop() {
        playJob?.cancel()
        playJob = scope.launch(Dispatchers.Default) {
            try {
                audioTrack?.play()
            } catch (_: Exception) {
                initAudioTrack()
                audioTrack?.play()
            }

            var noteIndex = 0
            val melodyNotes = getMelodyNotes(activeMelodyType)

            while (isActive && _isPlaying.value) {
                val currentNote = melodyNotes[noteIndex % melodyNotes.size]
                val noteDurationMs = ((currentNote.durationMs / playbackSpeedMultiplier)).toLong().coerceAtLeast(80L)

                // Synthesize short buffer of brass harmonic PCM
                val numSamples = (sampleRate * (noteDurationMs / 1000.0)).toInt().coerceAtLeast(200)
                val buffer = ShortArray(numSamples)
                val freq = currentNote.frequencyHz
                val bassFreq = currentNote.bassFreqHz

                // Equalizer Gains (dB to linear scale: 10^(dB / 20))
                val isEqOn = equalizerState.isEnabled
                val drumGain = if (isEqOn) {
                    Math.pow(10.0, equalizerState.bandLevels.getOrElse(0) { 0f }.toDouble() / 20.0) * (1.0 + equalizerState.bassBoost * 1.2)
                } else 1.0

                val bassGain = if (isEqOn) {
                    Math.pow(10.0, equalizerState.bandLevels.getOrElse(1) { 0f }.toDouble() / 20.0) * (1.0 + equalizerState.bassBoost * 0.8)
                } else 1.0

                val leadGain = if (isEqOn) {
                    Math.pow(10.0, equalizerState.bandLevels.getOrElse(3) { 0f }.toDouble() / 20.0)
                } else 1.0

                val trebleSurround = if (isEqOn) {
                    Math.pow(10.0, equalizerState.bandLevels.getOrElse(4) { 0f }.toDouble() / 20.0) * (1.0 + equalizerState.surround3D * 0.5)
                } else 1.0

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    // Envelope attack & release
                    val envelope = when {
                        i < numSamples * 0.15 -> i / (numSamples * 0.15)
                        i > numSamples * 0.85 -> (numSamples - i) / (numSamples * 0.15)
                        else -> 1.0
                    }

                    // Brass lead (fundamental + rich harmonics)
                    val lead = if (freq > 0) {
                        (sin(2 * PI * freq * t) * 0.5 +
                                sin(4 * PI * freq * t) * 0.25 +
                                sin(6 * PI * freq * t) * 0.12 * trebleSurround) * leadGain
                    } else 0.0

                    // Sousaphone / Tuba walking bass
                    val bass = if (bassFreq > 0) {
                        (sin(2 * PI * bassFreq * t) * 0.45 +
                                sin(4 * PI * bassFreq * t) * 0.2) * bassGain
                    } else 0.0

                    // Calenda Drum percussion pulse (burst of noise + low kick)
                    val drum = if (currentNote.hasKick && i < numSamples * 0.25) {
                        sin(2 * PI * 65.0 * t) * 0.4 * (1.0 - (i / (numSamples * 0.25))) * drumGain
                    } else 0.0

                    val sampleValue = ((lead + bass + drum) * envelope * 0.35 * Short.MAX_VALUE).toInt()
                    buffer[i] = sampleValue.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                try {
                    audioTrack?.write(buffer, 0, buffer.size)
                } catch (_: Exception) {}

                noteIndex++

                // Update position every ~1 sec
                if (noteIndex % 3 == 0) {
                    val nextSec = _currentPositionSec.value + 1
                    if (nextSec >= songDurationSec) {
                        _currentPositionSec.value = 0
                    } else {
                        _currentPositionSec.value = nextSec
                    }
                }
            }
        }
    }

    private fun startVisualizerLoop() {
        visualizerJob?.cancel()
        visualizerJob = scope.launch(Dispatchers.Default) {
            var step = 0
            while (isActive && _isPlaying.value) {
                step++
                val bars = MutableList(16) { index ->
                    val wave = sin((step * 0.35) + (index * 0.5)).toFloat()
                    val bassBoost = if (index in 0..4) 0.35f else 0.15f
                    val normalized = ((wave + 1f) / 2f * 0.65f + bassBoost).coerceIn(0.12f, 0.95f)
                    normalized
                }
                _visualizerAmplitudes.value = bars
                delay(80)
            }
        }
    }

    fun playSingleInstrument(instrument: BrassInstrument) {
        scope.launch(Dispatchers.Default) {
            val numSamples = (sampleRate * 0.5).toInt()
            val buffer = ShortArray(numSamples)
            val freq = instrument.noteFreq

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val envelope = when {
                    i < numSamples * 0.1 -> i / (numSamples * 0.1)
                    i > numSamples * 0.7 -> (numSamples - i) / (numSamples * 0.3)
                    else -> 1.0
                }
                val sample = when (instrument) {
                    BrassInstrument.BOMBO -> {
                        (sin(2 * PI * 58.0 * t) + sin(2 * PI * 116.0 * t) * 0.4) * envelope * 0.7
                    }
                    BrassInstrument.PLATILLOS -> {
                        // High frequency crash
                        (sin(2 * PI * 1800.0 * t) + sin(2 * PI * 3200.0 * t) * 0.5 + Math.random() * 0.4) * envelope * 0.4
                    }
                    BrassInstrument.SOUSAFONO -> {
                        (sin(2 * PI * freq * t) + sin(4 * PI * freq * t) * 0.4) * envelope * 0.6
                    }
                    else -> {
                        (sin(2 * PI * freq * t) * 0.6 + sin(4 * PI * freq * t) * 0.3 + sin(6 * PI * freq * t) * 0.15) * envelope * 0.5
                    }
                }
                buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }

            try {
                val tempTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()
                tempTrack.write(buffer, 0, buffer.size)
                tempTrack.play()
                delay(600)
                tempTrack.release()
            } catch (_: Exception) {}
        }
    }

    private data class Note(
        val frequencyHz: Double,
        val bassFreqHz: Double,
        val durationMs: Int,
        val hasKick: Boolean = false
    )

    private fun getMelodyNotes(melodyType: MelodyType): List<Note> {
        return when (melodyType) {
            MelodyType.JARABE_MIXE -> listOf(
                Note(466.16, 116.54, 300, true), // Bb4 / Bb2
                Note(523.25, 116.54, 300, false), // C5
                Note(587.33, 146.83, 300, true), // D5 / D3
                Note(700.00, 146.83, 450, false), // F5
                Note(587.33, 116.54, 250, true), // D5
                Note(466.16, 116.54, 500, true), // Bb4
                Note(392.00, 98.00, 300, true), // G4
                Note(466.16, 116.54, 450, false)
            )
            MelodyType.SON_CALENDA -> listOf(
                Note(349.23, 87.31, 240, true), // F4 / F2
                Note(440.00, 87.31, 240, false), // A4
                Note(523.25, 130.81, 240, true), // C5 / C3
                Note(587.33, 130.81, 240, false), // D5
                Note(523.25, 87.31, 240, true),
                Note(440.00, 87.31, 240, false),
                Note(349.23, 116.54, 480, true),
                Note(392.00, 116.54, 240, false)
            )
            MelodyType.CUMBIA_TROPICAL -> listOf(
                Note(311.13, 77.78, 220, true), // Eb4 / Eb2
                Note(0.0, 77.78, 110, false), // Rest
                Note(392.00, 116.54, 220, true), // G4 / Bb2
                Note(466.16, 116.54, 220, false), // Bb4
                Note(523.25, 77.78, 330, true), // C5 / Eb2
                Note(466.16, 77.78, 220, false),
                Note(392.00, 103.83, 220, true),
                Note(311.13, 77.78, 440, true)
            )
            MelodyType.DANZON_GALA -> listOf(
                Note(415.30, 103.83, 400, true), // Ab4
                Note(466.16, 103.83, 300, false), // Bb4
                Note(523.25, 130.81, 500, true), // C5
                Note(622.25, 130.81, 350, false), // Eb5
                Note(523.25, 103.83, 400, true),
                Note(415.30, 103.83, 600, false)
            )
            MelodyType.MARCHA_FESTIVA -> listOf(
                Note(466.16, 116.54, 280, true),
                Note(466.16, 116.54, 280, true),
                Note(587.33, 146.83, 280, false),
                Note(700.00, 174.61, 560, true),
                Note(587.33, 146.83, 280, false),
                Note(466.16, 116.54, 560, true)
            )
            MelodyType.CHILENA_OAXACA -> listOf(
                Note(392.00, 98.00, 260, true), // G4 / G2
                Note(493.88, 98.00, 260, false), // B4
                Note(587.33, 146.83, 260, true), // D5 / D3
                Note(659.25, 146.83, 260, false), // E5
                Note(587.33, 98.00, 260, true),
                Note(493.88, 98.00, 260, false),
                Note(392.00, 98.00, 520, true)
            )
        }
    }

    fun release() {
        pause()
        try {
            audioTrack?.release()
        } catch (_: Exception) {}
        audioTrack = null
    }
}
