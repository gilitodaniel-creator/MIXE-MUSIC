package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.firestore.FirestoreMusicRepository
import com.example.data.model.MelodyType
import com.example.data.model.Song
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FirestoreMusicRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun unauthenticated_cannot_publish_song() = runBlocking {
        if (!isEmulatorRunning()) return@runBlocking

        auth.signOut()
        val repo = FirestoreMusicRepository(firestore)
        val song = Song(
            id = "test_song_1",
            title = "Son de Prueba",
            artist = "Banda Filarmónica",
            album = "Álbum",
            durationSec = 200,
            genre = "Son y Jarabe",
            coverResId = 0,
            community = "Ayutla Mixe",
            culturalNotes = "Notas tradicionales",
            melodyType = MelodyType.JARABE_MIXE
        )

        val result = repo.publishSong(song, "JARABE_MIXE")
        assertFalse(result.isSuccess)
    }

    @Test
    fun authenticated_producer_can_publish_and_read() = runBlocking {
        if (!isEmulatorRunning()) return@runBlocking

        val uid = signInTestUser("gilito.daniel@gmail.com")
        assertTrue(uid.isNotEmpty())

        val repo = FirestoreMusicRepository(firestore)
        val song = Song(
            id = "test_song_prod_2",
            title = "Jarabe de la Sierra Mixe",
            ayuukTitle = "Ayuuk Jä'äy",
            artist = "Banda Filarmónica de Tlahuitoltepec",
            album = "GD Music Nube",
            durationSec = 240,
            genre = "Son y Jarabe",
            coverResId = 0,
            community = "Tlahuitoltepec",
            culturalNotes = "Grabación comunitaria en atrio",
            melodyType = MelodyType.JARABE_MIXE
        )

        val result = repo.publishSong(song, "JARABE_MIXE")
        assertTrue(result.isSuccess)
    }
}
