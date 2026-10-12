package com.example.audio

data class EqBandInfo(
    val index: Int,
    val frequencyLabel: String,
    val instrumentFocus: String,
    val rangeMin: Float = -12f,
    val rangeMax: Float = 12f
)

val EQ_BANDS_INFO = listOf(
    EqBandInfo(0, "60 Hz", "Sub-Graves • Bombo y Tuba"),
    EqBandInfo(1, "230 Hz", "Graves • Sousáfono"),
    EqBandInfo(2, "910 Hz", "Medios • Clarinetes"),
    EqBandInfo(3, "3.6 kHz", "Medios-Altos • Trompetas"),
    EqBandInfo(4, "14 kHz", "Agudos • Platillos y Campanas")
)

data class EqualizerPreset(
    val name: String,
    val bandLevels: List<Float>, // -12f to +12f for bands [60Hz, 230Hz, 910Hz, 3.6kHz, 14kHz]
    val bassBoost: Float = 0.5f, // 0f to 1.0f
    val surround3D: Float = 0.3f, // 0f to 1.0f
    val description: String = ""
)

data class EqualizerState(
    val isEnabled: Boolean = true,
    val activePresetName: String = "Mixe Brass Master",
    val bandLevels: List<Float> = listOf(4f, 6f, 2f, 4f, 5f),
    val bassBoost: Float = 0.55f,
    val surround3D: Float = 0.35f
)

val PROFESSIONAL_EQ_PRESETS = listOf(
    EqualizerPreset(
        name = "Mixe Brass Master",
        bandLevels = listOf(4f, 6f, 2f, 5f, 6f),
        bassBoost = 0.55f,
        surround3D = 0.4f,
        description = "Calibración estelar para trompetas y clarinetes con presencia y potencia filarmónica."
    ),
    EqualizerPreset(
        name = "Calenda en Vivo",
        bandLevels = listOf(8f, 7f, 3f, 6f, 7f),
        bassBoost = 0.85f,
        surround3D = 0.75f,
        description = "Impacto expansivo de fiesta patronal al aire libre con retumbe de sousáfono y bombo."
    ),
    EqualizerPreset(
        name = "Danzón de Gala",
        bandLevels = listOf(1f, 3f, 6f, 5f, 3f),
        bassBoost = 0.2f,
        surround3D = 0.3f,
        description = "Calidez acústica pura, balance fino en maderas y armonía tradicional de concierto."
    ),
    EqualizerPreset(
        name = "Cumbia Serrana",
        bandLevels = listOf(7f, 6f, 1f, 4f, 5f),
        bassBoost = 0.75f,
        surround3D = 0.45f,
        description = "Ritmo bailable contagioso con bajo profundo y agudos brillantes para fiesta."
    ),
    EqualizerPreset(
        name = "Marcha Solemne",
        bandLevels = listOf(5f, 6f, 4f, 3f, 4f),
        bassBoost = 0.45f,
        surround3D = 0.5f,
        description = "Cuerpo majestuoso y reverencia ceremonial para procesiones y actos cívicos."
    ),
    EqualizerPreset(
        name = "Plano / Estudio",
        bandLevels = listOf(0f, 0f, 0f, 0f, 0f),
        bassBoost = 0f,
        surround3D = 0f,
        description = "Respuesta de frecuencia neutra para monitoreo y masterización transparente."
    )
)
