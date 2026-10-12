package com.example.data.repository

import com.example.data.db.FavoriteEntity
import com.example.data.db.MusicDao
import com.example.data.db.PlaybackHistoryEntity
import com.example.data.db.PlaylistEntity
import com.example.data.db.PlaylistSongCrossRef
import com.example.data.model.BandProfile
import com.example.data.model.FiestaEvent
import com.example.data.model.MOCK_BANDS
import com.example.data.model.MOCK_FIESTAS
import com.example.data.model.MOCK_SERVICES
import com.example.data.model.MOCK_SONGS
import com.example.data.model.Song
import com.example.data.model.StudioService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MusicRepository(private val musicDao: MusicDao) {

    fun getAllSongs(): List<Song> = MOCK_SONGS

    fun getSongById(id: String): Song? = MOCK_SONGS.find { it.id == id }

    fun getBands(): List<BandProfile> = MOCK_BANDS

    fun getFiestas(): List<FiestaEvent> = MOCK_FIESTAS

    fun getServices(): List<StudioService> = MOCK_SERVICES

    val favoriteSongIds: Flow<List<String>> = musicDao.getAllFavoriteIds()

    val favoriteSongs: Flow<List<Song>> = favoriteSongIds.map { ids ->
        ids.mapNotNull { id -> getSongById(id) }
    }

    suspend fun toggleFavorite(songId: String, isCurrentlyFavorite: Boolean) {
        if (isCurrentlyFavorite) {
            musicDao.removeFavorite(songId)
        } else {
            musicDao.addFavorite(FavoriteEntity(songId = songId))
        }
    }

    val playlists: Flow<List<PlaylistEntity>> = musicDao.getAllPlaylists()

    suspend fun createPlaylist(name: String, description: String): Long {
        return musicDao.insertPlaylist(
            PlaylistEntity(name = name, description = description)
        )
    }

    suspend fun deletePlaylist(playlistId: Long) {
        musicDao.deletePlaylist(playlistId)
    }

    suspend fun addSongToPlaylist(playlistId: Long, songId: String) {
        musicDao.addSongToPlaylist(PlaylistSongCrossRef(playlistId, songId))
    }

    suspend fun removeSongFromPlaylist(playlistId: Long, songId: String) {
        musicDao.removeSongFromPlaylist(playlistId, songId)
    }

    fun getSongsForPlaylist(playlistId: Long): Flow<List<Song>> {
        return musicDao.getSongIdsForPlaylist(playlistId).map { ids ->
            ids.mapNotNull { id -> getSongById(id) }
        }
    }

    val recentHistorySongs: Flow<List<Song>> = musicDao.getRecentHistorySongIds().map { ids ->
        ids.mapNotNull { id -> getSongById(id) }
    }

    suspend fun recordPlayed(songId: String) {
        musicDao.recordHistory(PlaybackHistoryEntity(songId = songId))
    }
}
