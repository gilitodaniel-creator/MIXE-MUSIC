package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        FavoriteEntity::class,
        PlaylistEntity::class,
        PlaylistSongCrossRef::class,
        PlaybackHistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun musicDao(): MusicDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gd_music_mixe.db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Prepopulate default favorite and playlist
                        scope.launch(Dispatchers.IO) {
                            val dao = getDatabase(context, scope).musicDao()
                            dao.addFavorite(FavoriteEntity("song_1"))
                            dao.addFavorite(FavoriteEntity("song_2"))
                            val playlistId = dao.insertPlaylist(
                                PlaylistEntity(
                                    name = "Lo Mejor de la Sierra Mixe",
                                    description = "Sones inmortales grabados por GD Music Producciones"
                                )
                            )
                            dao.addSongToPlaylist(PlaylistSongCrossRef(playlistId, "song_1", 0))
                            dao.addSongToPlaylist(PlaylistSongCrossRef(playlistId, "song_2", 1))
                            dao.addSongToPlaylist(PlaylistSongCrossRef(playlistId, "song_3", 2))

                            val calendaPlaylistId = dao.insertPlaylist(
                                PlaylistEntity(
                                    name = "Calendas y Fiestas 2026",
                                    description = "Para zapatear y celebrar en comunidad"
                                )
                            )
                            dao.addSongToPlaylist(PlaylistSongCrossRef(calendaPlaylistId, "song_2", 0))
                            dao.addSongToPlaylist(PlaylistSongCrossRef(calendaPlaylistId, "song_3", 1))
                            dao.addSongToPlaylist(PlaylistSongCrossRef(calendaPlaylistId, "song_6", 2))
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
