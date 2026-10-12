package com.example.data.model

import com.example.R

enum class MelodyType {
    JARABE_MIXE,
    SON_CALENDA,
    CUMBIA_TROPICAL,
    DANZON_GALA,
    MARCHA_FESTIVA,
    CHILENA_OAXACA
}

data class Song(
    val id: String,
    val title: String,
    val ayuukTitle: String? = null,
    val artist: String,
    val album: String,
    val durationSec: Int,
    val genre: String,
    val coverResId: Int,
    val year: Int = 2026,
    val bpm: Int = 118,
    val musicalKey: String = "Bb Mayor",
    val community: String = "Sierra Mixe, Oaxaca",
    val culturalNotes: String,
    val melodyType: MelodyType
)

val MOCK_SONGS = listOf(
    Song(
        id = "song_1",
        title = "Jarabe Mixe Tradicional",
        ayuukTitle = "Ayuuk Jä'äy Xojp",
        artist = "Banda Filarmónica de Santa María Tlahuitoltepec",
        album = "Sones y Jarabes Mixes Oaxaqueños",
        durationSec = 254,
        genre = "Son y Jarabe",
        coverResId = R.drawable.img_sones_jarabes_1791516014406,
        year = 2026,
        bpm = 124,
        musicalKey = "Bb Mayor",
        community = "Santa María Tlahuitoltepec, Mixe",
        culturalNotes = "El Jarabe Mixe es la máxima expresión sonora del pueblo jamás conquistado. Con sus giros melódicos en trompetas y clarinete, representa el viento de las montañas y la fuerza comunitaria.",
        melodyType = MelodyType.JARABE_MIXE
    ),
    Song(
        id = "song_2",
        title = "Gran Calenda de San Pedro y San Pablo",
        ayuukTitle = "Mäjtën Tääk Ayutla",
        artist = "Banda Municipal de Ayutla Mixe",
        album = "Calenda y Cumbia Mixe Festival 2026",
        durationSec = 210,
        genre = "En Vivo / Calenda",
        coverResId = R.drawable.img_fiesta_calenda_1791516031670,
        year = 2026,
        bpm = 132,
        musicalKey = "F Mayor",
        community = "San Pedro y San Pablo Ayutla",
        culturalNotes = "Grabado en vivo por GD Music MIXE Producciones durante la fiesta patronal. Acompaña a las marmotas gigantes y canasteras en el recorrido festivo por las calles empedradas.",
        melodyType = MelodyType.SON_CALENDA
    ),
    Song(
        id = "song_3",
        title = "Cumbia del Cerro Zempoaltépetl",
        ayuukTitle = "Ipxukp Zempoaltépetl",
        artist = "Banda Tierra Mixe de Tamazulápam",
        album = "Calenda y Cumbia Mixe Festival 2026",
        durationSec = 198,
        genre = "Cumbia Mixe",
        coverResId = R.drawable.img_fiesta_calenda_1791516031670,
        year = 2025,
        bpm = 110,
        musicalKey = "Eb Mayor",
        community = "Tamazulápam del Espíritu Santo",
        culturalNotes = "Fusión de cumbia tropical con los potentes metales de la Sierra Norte. El sousáfono marca el sabroso bajo caminante mientras las trompetas prenden el baile.",
        melodyType = MelodyType.CUMBIA_TROPICAL
    ),
    Song(
        id = "song_4",
        title = "Danzón de Gala Oaxaqueño",
        ayuukTitle = "Mä'äy Jää'y",
        artist = "Orquesta Filarmónica Ayuuk",
        album = "Joyas de la Sierra Norte",
        durationSec = 285,
        genre = "Danzón",
        coverResId = R.drawable.img_mixe_band_hero_1791515996753,
        year = 2025,
        bpm = 92,
        musicalKey = "Ab Mayor",
        community = "Juquila Mixes",
        culturalNotes = "Elegante pieza para noche de gala y quema del castillo de fuegos artificiales. Solo de clarinete virtuoso que evoca la neblina matutina de la serranía.",
        melodyType = MelodyType.DANZON_GALA
    ),
    Song(
        id = "song_5",
        title = "Marcha de Fiesta de los Reyes",
        ayuukTitle = "Reyësh Xäjpët",
        artist = "Banda Filarmónica de Santa María Tlahuitoltepec",
        album = "Sones y Jarabes Mixes Oaxaqueños",
        durationSec = 180,
        genre = "Marcha Festiva",
        coverResId = R.drawable.img_sones_jarabes_1791516014406,
        year = 2026,
        bpm = 116,
        musicalKey = "Bb Mayor",
        community = "Tlahuitoltepec",
        culturalNotes = "Marcha alegre ejecutada a la llegada de las bandas hermanas invitadas al atrio del templo en la fiesta anual comunitaria.",
        melodyType = MelodyType.MARCHA_FESTIVA
    ),
    Song(
        id = "song_6",
        title = "Chilena de la Sierra y Costa",
        ayuukTitle = "Nääy Jä'äy",
        artist = "Banda Auténtica de San Lucas Camotlán",
        album = "Calenda y Cumbia Mixe Festival 2026",
        durationSec = 222,
        genre = "Chilena",
        coverResId = R.drawable.img_fiesta_calenda_1791516031670,
        year = 2026,
        bpm = 128,
        musicalKey = "G Mayor",
        community = "Sierra Sur / Mixe Alta",
        culturalNotes = "El pañuelo al aire y el zapateado vigoroso se entrelazan con los metales en esta chilena clásica remasterizada en estudio GD Music.",
        melodyType = MelodyType.CHILENA_OAXACA
    )
)
