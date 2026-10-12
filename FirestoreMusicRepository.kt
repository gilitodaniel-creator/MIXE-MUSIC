package com.example.data.firestore

import android.content.Context
import com.example.R
import com.example.data.model.Song
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestoreMusicRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth
    private val publishedSongsCollection = db.collection("published_songs")
    private val usersCollection = db.collection("users")

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("Debes iniciar sesión con Google para acceder a la nube de GD Music.")
    }

    fun observePublishedSongs(): Flow<List<Song>> = flow {
        if (auth.currentUser == null) {
            emit(emptyList())
            return@flow
        }

        val path = publishedSongsCollection.path
        val stream = publishedSongsCollection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(PublishedSongDto::class.java)?.toSong()
                }
            }
            .catch { error ->
                if (error is Exception) {
                    FirestoreErrorHandler.handleFirestoreError(error, OperationType.LIST, path)
                }
                throw error
            }

        emitAll(stream)
    }

    suspend fun publishSong(
        song: Song,
        melodyTypeStr: String,
        audioUrl: String? = null
    ): Result<Unit> {
        return try {
            val uid = requireUserId()
            val user = auth.currentUser
            val docRef = publishedSongsCollection.document(song.id)
            val path = docRef.path

            val dto = PublishedSongDto(
                id = song.id,
                title = song.title,
                ayuukTitle = song.ayuukTitle,
                artist = song.artist,
                album = song.album,
                community = song.community,
                genre = song.genre,
                audioUrl = audioUrl,
                melodyType = melodyTypeStr,
                durationSec = song.durationSec,
                musicalKey = song.musicalKey,
                bpm = song.bpm,
                culturalNotes = song.culturalNotes,
                publisherUid = uid,
                publisherName = user?.displayName ?: "Productor GD Music",
                publisherEmail = user?.email ?: ""
            )

            val payload = dto.toMap() + mapOf(
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )

            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: Exception) {
            val msg = FirestoreErrorHandler.handleFirestoreError(e, OperationType.CREATE, publishedSongsCollection.path)
            Result.failure(Exception(msg))
        }
    }

    suspend fun ensureUserProfile(): Result<Unit> {
        return try {
            val user = auth.currentUser ?: return Result.failure(IllegalStateException("No autenticado"))
            val userDoc = usersCollection.document(user.uid)
            val snapshot = userDoc.get().await()

            if (!snapshot.exists()) {
                val isProducer = user.email?.equals("gilito.daniel@gmail.com", ignoreCase = true) == true
                val payload = mapOf(
                    "userId" to user.uid,
                    "displayName" to (user.displayName ?: "Oyente Mixe"),
                    "email" to (user.email ?: ""),
                    "role" to if (isProducer) "producer" else "listener",
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                userDoc.set(payload).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            val msg = FirestoreErrorHandler.handleFirestoreError(e, OperationType.CREATE, usersCollection.path)
            Result.failure(Exception(msg))
        }
    }

    suspend fun isUserProducer(): Boolean {
        val user = auth.currentUser ?: return false
        if (user.email?.equals("gilito.daniel@gmail.com", ignoreCase = true) == true) return true
        return try {
            val snapshot = usersCollection.document(user.uid).get().await()
            snapshot.getString("role") == "producer"
        } catch (_: Exception) {
            false
        }
    }
}
