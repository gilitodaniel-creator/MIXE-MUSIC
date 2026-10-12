package com.example.data.model

import com.example.R

data class BandProfile(
    val id: String,
    val name: String,
    val municipality: String,
    val director: String,
    val foundedYear: Int,
    val memberCount: Int,
    val imageResId: Int,
    val bio: String,
    val featuredSongId: String
)

data class FiestaEvent(
    val id: String,
    val title: String,
    val community: String,
    val dateRange: String,
    val description: String,
    val status: String = "Transmisión en Vivo GD"
)

data class StudioService(
    val id: String,
    val title: String,
    val iconName: String,
    val subtitle: String,
    val description: String
)

val MOCK_BANDS = listOf(
    BandProfile(
        id = "band_tlahui",
        name = "Banda Filarmónica de Santa María Tlahuitoltepec",
        municipality = "Santa María Tlahuitoltepec, Mixe",
        director = "Mtro. Leovigildo Martínez",
        foundedYear = 1977,
        memberCount = 42,
        imageResId = R.drawable.img_mixe_band_hero_1791515996753,
        bio = "Cuna de la música de viento en Oaxaca. Formados en la mística del CECAM, sus ejecuciones de sones y jarabes son patrimonio sonoro de los pueblos originarios.",
        featuredSongId = "song_1"
    ),
    BandProfile(
        id = "band_ayutla",
        name = "Banda Municipal de San Pedro y San Pablo Ayutla",
        municipality = "Ayutla Mixe, Oaxaca",
        director = "Mtro. Floriberto Díaz",
        foundedYear = 1985,
        memberCount = 38,
        imageResId = R.drawable.img_fiesta_calenda_1791516031670,
        bio = "Reconocida por la energía contagiosa de sus calendas y la precisión armónica de sus arreglos de fiesta patronal en toda la Sierra Norte.",
        featuredSongId = "song_2"
    ),
    BandProfile(
        id = "band_tamazulapam",
        name = "Banda Tierra Mixe de Tamazulápam",
        municipality = "Tamazulápam del Espíritu Santo",
        director = "Mtro. Cándido Romero",
        foundedYear = 1998,
        memberCount = 35,
        imageResId = R.drawable.img_sones_jarabes_1791516014406,
        bio = "Especialistas en cumbias serranas, pasodobles y danzones con sousáfono y metales de alto poder. Grabaciones exclusivas de GD Music.",
        featuredSongId = "song_3"
    )
)

val MOCK_FIESTAS = listOf(
    FiestaEvent(
        id = "fiesta_1",
        title = "Fiesta Patronal de San Pedro y San Pablo",
        community = "Ayutla Mixe, Oax.",
        dateRange = "28 - 30 de Junio 2026",
        description = "Magna calenda con bandas filarmónicas invitadas, quema de castillos pirotécnicos y concierto en el atrio parroquial."
    ),
    FiestaEvent(
        id = "fiesta_2",
        title = "Fiesta de la Asunción de María",
        community = "Santa María Tlahuitoltepec",
        dateRange = "13 - 16 de Agosto 2026",
        description = "Encuentro de bandas de viento, sones y jarabes mixes tradicionales en el templo comunal con transmisión en directo GD Music."
    ),
    FiestaEvent(
        id = "fiesta_3",
        title = "Fiesta del Espíritu Santo",
        community = "Tamazulápam Mixe, Oax.",
        dateRange = "Pentecostés 2026",
        description = "Baile de calenda tradicional, danzón de gala y concurso regional de bandas filarmónicas juveniles."
    ),
    FiestaEvent(
        id = "fiesta_4",
        title = "Festividad del Señor de las Misericordias",
        community = "Juquila Mixes, Oax.",
        dateRange = "15 - 18 de Enero 2027",
        description = "Procesión solemne con chirimía y banda completa, seguida de baile popular de cumbias y chilenas mixes."
    )
)

val MOCK_SERVICES = listOf(
    StudioService(
        id = "srv_1",
        title = "Grabación en Estudio Móvil",
        iconName = "Mic",
        subtitle = "32 Canales en la Sierra Mixe",
        description = "Llegamos hasta tu comunidad con equipo profesional de microfonía condenser para bandas completas de 20 a 50 músicos."
    ),
    StudioService(
        id = "srv_2",
        title = "Producción de Video en Vivo 4K",
        iconName = "Videocam",
        subtitle = "Calendas y Fiestas Patronales",
        description = "Cobertura multicámara y dron para capturar la majestuosidad de la calenda, atrios comunales y pirotecnia tradicional."
    ),
    StudioService(
        id = "srv_3",
        title = "Mezcla & Masterización para Streaming",
        iconName = "Equalizer",
        subtitle = "Sonido nítido para Spotify y YouTube",
        description = "Optimización acústica diseñada específicamente para la resonancia de metales (trompetas, tubas, saxores y clarinetes)."
    ),
    StudioService(
        id = "srv_4",
        title = "Distribución Digital & Portadas",
        iconName = "Album",
        subtitle = "Diseño gráfico con arte Ayuuk",
        description = "Lleva la música de tu banda a todo México, Estados Unidos y el mundo con portadas exclusivas y metadatos culturales."
    )
)
