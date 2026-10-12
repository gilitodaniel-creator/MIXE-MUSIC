package com.example.data.firestore

import com.example.R
import com.example.data.model.MelodyType
import com.example.data.model.Song
import com.google.firebase.Timestamp

data class PublishedSongDto(
    val id: String = "",
    val title: String = "",
    val ayuukTitle: String? = null,
    val artist: String = "",
    val album: String = "GD Music Nube",
    val community: String = "",
    val genre: String = "Son y Jarabe",
    val audioUrl: String? = null,
    val melodyType: String = "JARABE_MIXE",
    val durationSec: Int = 180,
    val musicalKey: String = "Bb Mayor",
    val bpm: Int = 120,
    val culturalNotes: String = "",
    val publisherUid: String = "",
    val publisherName: String = "",
    val publisherEmail: String = "",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    fun toSong(): Song {
        val parsedMelody = try {
            MelodyType.valueOf(melodyType)
        } catch (_: Exception) {
            MelodyType.JARABE_MIXE
        }

        val cover = when (parsedMelody) {
            MelodyType.SON_CALENDA, MelodyType.CUMBIA_TROPICAL -> R.drawable.img_fiesta_calenda_1791516031670
            MelodyType.DANZON_GALA -> R.drawable.img_mixe_band_hero_1791515996753
            else -> R.drawable.img_sones_jarabes_1791516014406
        }

        return Song(
            id = id,
            title = title,
            ayuukTitle = ayuukTitle,
            artist = artist,
            album = album,
            durationSec = durationSec,
            genre = genre,
            coverResId = cover,
            year = 2026,
            bpm = bpm,
            musicalKey = musicalKey,
            community = community,
            culturalNotes = culturalNotes,
            melodyType = parsedMelody
        )
    }

    fun toMap(): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>(
            "id" to id,
            "title" to title,
            "artist" to artist,
            "album" to album,
            "community" to community,
            "genre" to genre,
            "melodyType" to melodyType,
            "durationSec" to durationSec,
            "musicalKey" to musicalKey,
            "bpm" to bpm,
            "culturalNotes" to culturalNotes,
            "publisherUid" to publisherUid,
            "publisherName" to publisherName,
            "publisherEmail" to publisherEmail
        )
        if (!ayuukTitle.isNullOrBlank()) map["ayuukTitle"] = ayuukTitle
        if (!audioUrl.isNullOrBlank()) map["audioUrl"] = audioUrl
        return map
    }
}

data class UserProfileDto(
    val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    val role: String = "listener",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "userId" to userId,
        "displayName" to displayName,
        "email" to email,
        "role" to role
    )
}
