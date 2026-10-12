package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.BrassInstrument
import com.example.audio.EqualizerState
import com.example.audio.MixeAudioEngine
import com.example.data.db.AppDatabase
import com.example.data.db.PlaylistEntity
import com.example.data.firestore.FirestoreMusicRepository
import com.example.data.model.BandProfile
import com.example.data.model.FiestaEvent
import com.example.data.model.MOCK_SONGS
import com.example.data.model.MelodyType
import com.example.data.model.Song
import com.example.data.model.StudioService
import com.example.data.repository.MusicRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PlayerUiState(
    val currentSong: Song = MOCK_SONGS[0],
    val isPlaying: Boolean = false,
    val currentPositionSec: Int = 0,
    val isShuffle: Boolean = false,
    val isRepeat: Boolean = false,
    val playbackSpeed: Float = 1.0f,
    val equalizerPreset: String = "Mixe Brass Punch",
    val visualizerBars: List<Float> = List(16) { 0.2f },
    val isExpandedPlayer: Boolean = false
)

class MusicViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = MusicRepository(database.musicDao())
    private val firestoreRepository = FirestoreMusicRepository(application)
    private val audioEngine = MixeAudioEngine(viewModelScope)

    private val _playerState = MutableStateFlow(PlayerUiState())
    val playerState: StateFlow<PlayerUiState> = _playerState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _activeGenre = MutableStateFlow("Todos")
    val activeGenre: StateFlow<String> = _activeGenre.asStateFlow()

    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    private val _selectedBand = MutableStateFlow<BandProfile?>(null)
    val selectedBand: StateFlow<BandProfile?> = _selectedBand.asStateFlow()

    private val _selectedSongForDetails = MutableStateFlow<Song?>(null)
    val selectedSongForDetails: StateFlow<Song?> = _selectedSongForDetails.asStateFlow()

    private val _bookingNotice = MutableStateFlow<String?>(null)
    val bookingNotice: StateFlow<String?> = _bookingNotice.asStateFlow()

    private val _showPublisherScreen = MutableStateFlow(false)
    val showPublisherScreen: StateFlow<Boolean> = _showPublisherScreen.asStateFlow()

    private val _equalizerState = MutableStateFlow(EqualizerState())
    val equalizerState: StateFlow<EqualizerState> = _equalizerState.asStateFlow()

    private val _showEqualizerDialog = MutableStateFlow(false)
    val showEqualizerDialog: StateFlow<Boolean> = _showEqualizerDialog.asStateFlow()

    fun updateEqualizer(state: EqualizerState) {
        _equalizerState.value = state
        audioEngine.updateEqualizer(state)
    }

    fun setShowEqualizerDialog(show: Boolean) {
        _showEqualizerDialog.value = show
    }

    fun setEqualizerEnabled(enabled: Boolean) {
        val updated = _equalizerState.value.copy(isEnabled = enabled)
        updateEqualizer(updated)
    }

    fun setEqualizerBandLevel(bandIndex: Int, levelDb: Float) {
        val currentLevels = _equalizerState.value.bandLevels.toMutableList()
        if (bandIndex in currentLevels.indices) {
            currentLevels[bandIndex] = levelDb.coerceIn(-12f, 12f)
            val updated = _equalizerState.value.copy(
                bandLevels = currentLevels,
                activePresetName = "Personalizado"
            )
            updateEqualizer(updated)
        }
    }

    fun setEqualizerBassBoost(boost: Float) {
        val updated = _equalizerState.value.copy(bassBoost = boost.coerceIn(0f, 1f))
        updateEqualizer(updated)
    }

    fun setEqualizerSurround3D(surround: Float) {
        val updated = _equalizerState.value.copy(surround3D = surround.coerceIn(0f, 1f))
        updateEqualizer(updated)
    }

    fun applyEqualizerPreset(preset: com.example.audio.EqualizerPreset) {
        val updated = _equalizerState.value.copy(
            activePresetName = preset.name,
            bandLevels = preset.bandLevels,
            bassBoost = preset.bassBoost,
            surround3D = preset.surround3D
        )
        updateEqualizer(updated)
        _playerState.value = _playerState.value.copy(equalizerPreset = preset.name)
    }

    fun resetEqualizerFlat() {
        val flat = EqualizerState(
            isEnabled = true,
            activePresetName = "Plano / Estudio",
            bandLevels = listOf(0f, 0f, 0f, 0f, 0f),
            bassBoost = 0f,
            surround3D = 0f
        )
        updateEqualizer(flat)
        _playerState.value = _playerState.value.copy(equalizerPreset = "Plano / Estudio")
    }

    // Publishing states
    val isPublishing = MutableStateFlow(false)
    val publishSuccessMessage = MutableStateFlow<String?>(null)
    val publishErrorMessage = MutableStateFlow<String?>(null)

    // Cloud published songs stream
    private val cloudPublishedSongs = firestoreRepository.observePublishedSongs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Merged song list (Cloud tracks first + Local catalog)
    val allSongs: StateFlow<List<Song>> = cloudPublishedSongs
        .combine(MutableStateFlow(repository.getAllSongs())) { cloud, local ->
            val cloudIds = cloud.map { it.id }.toSet()
            val filteredLocal = local.filter { it.id !in cloudIds }
            cloud + filteredLocal
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.getAllSongs())

    val favoriteSongIds: StateFlow<List<String>> = repository.favoriteSongIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val playlists: StateFlow<List<PlaylistEntity>> = repository.playlists
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentHistorySongs: StateFlow<List<Song>> = repository.recentHistorySongs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBands: List<BandProfile> = repository.getBands()
    val allFiestas: List<FiestaEvent> = repository.getFiestas()
    val allServices: List<StudioService> = repository.getServices()

    init {
        // Collect state from audioEngine
        viewModelScope.launch {
            audioEngine.isPlaying.collect { playing ->
                _playerState.value = _playerState.value.copy(isPlaying = playing)
            }
        }
        viewModelScope.launch {
            audioEngine.currentPositionSec.collect { pos ->
                _playerState.value = _playerState.value.copy(currentPositionSec = pos)
            }
        }
        viewModelScope.launch {
            audioEngine.visualizerAmplitudes.collect { bars ->
                _playerState.value = _playerState.value.copy(visualizerBars = bars)
            }
        }

        // Ensure user profile in Firestore
        viewModelScope.launch {
            firestoreRepository.ensureUserProfile()
        }
    }

    fun playSong(song: Song) {
        val current = _playerState.value.currentSong
        if (current.id == song.id && _playerState.value.isPlaying) {
            audioEngine.pause()
        } else if (current.id == song.id) {
            audioEngine.resume()
        } else {
            _playerState.value = _playerState.value.copy(
                currentSong = song,
                currentPositionSec = 0
            )
            audioEngine.playSong(song.melodyType, song.durationSec)
            viewModelScope.launch {
                repository.recordPlayed(song.id)
            }
        }
    }

    fun togglePlayPause() {
        if (_playerState.value.isPlaying) {
            audioEngine.pause()
        } else {
            val song = _playerState.value.currentSong
            if (_playerState.value.currentPositionSec > 0) {
                audioEngine.resume()
            } else {
                audioEngine.playSong(song.melodyType, song.durationSec)
            }
        }
    }

    fun playNext() {
        val list = allSongs.value
        val currentIndex = list.indexOfFirst { it.id == _playerState.value.currentSong.id }
        val nextIndex = if (_playerState.value.isShuffle) {
            (0 until list.size).filter { it != currentIndex }.randomOrNull() ?: 0
        } else {
            (currentIndex + 1) % list.size
        }
        playSong(list[nextIndex])
    }

    fun playPrevious() {
        val list = allSongs.value
        val currentIndex = list.indexOfFirst { it.id == _playerState.value.currentSong.id }
        val prevIndex = if (currentIndex <= 0) list.size - 1 else currentIndex - 1
        playSong(list[prevIndex])
    }

    fun seekTo(seconds: Int) {
        audioEngine.seekTo(seconds)
    }

    fun toggleShuffle() {
        _playerState.value = _playerState.value.copy(isShuffle = !_playerState.value.isShuffle)
    }

    fun toggleRepeat() {
        _playerState.value = _playerState.value.copy(isRepeat = !_playerState.value.isRepeat)
    }

    fun setPlaybackSpeed(speed: Float) {
        _playerState.value = _playerState.value.copy(playbackSpeed = speed)
        audioEngine.setPlaybackSpeed(speed)
    }

    fun setEqualizerPreset(preset: String) {
        val found = com.example.audio.PROFESSIONAL_EQ_PRESETS.find { it.name.equals(preset, ignoreCase = true) }
        if (found != null) {
            applyEqualizerPreset(found)
        } else {
            _playerState.value = _playerState.value.copy(equalizerPreset = preset)
        }
    }

    fun setExpandedPlayer(expanded: Boolean) {
        _playerState.value = _playerState.value.copy(isExpandedPlayer = expanded)
    }

    fun setShowPublisherScreen(show: Boolean) {
        _showPublisherScreen.value = show
        publishSuccessMessage.value = null
        publishErrorMessage.value = null
    }

    fun setTab(index: Int) {
        _currentTab.value = index
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setActiveGenre(genre: String) {
        _activeGenre.value = genre
    }

    fun selectBand(band: BandProfile?) {
        _selectedBand.value = band
    }

    fun selectSongForDetails(song: Song?) {
        _selectedSongForDetails.value = song
    }

    fun toggleFavorite(songId: String) {
        val isFav = favoriteSongIds.value.contains(songId)
        viewModelScope.launch {
            repository.toggleFavorite(songId, isFav)
        }
    }

    fun createPlaylist(name: String, desc: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.createPlaylist(name, desc)
        }
    }

    fun addSongToPlaylist(playlistId: Long, songId: String) {
        viewModelScope.launch {
            repository.addSongToPlaylist(playlistId, songId)
        }
    }

    fun playSingleInstrument(instrument: BrassInstrument) {
        audioEngine.playSingleInstrument(instrument)
    }

    fun publishNewSong(
        title: String,
        ayuukTitle: String?,
        artist: String,
        community: String,
        genre: String,
        melodyTypeStr: String,
        durationSec: Int,
        bpm: Int,
        musicalKey: String,
        culturalNotes: String,
        audioUrl: String?
    ) {
        if (title.isBlank() || artist.isBlank() || community.isBlank() || culturalNotes.isBlank()) {
            publishErrorMessage.value = "Por favor completa todos los campos requeridos con asterisco (*)."
            return
        }

        val parsedMelody = try {
            MelodyType.valueOf(melodyTypeStr)
        } catch (_: Exception) {
            MelodyType.JARABE_MIXE
        }

        val generatedId = "pub_" + System.currentTimeMillis()
        val newSong = Song(
            id = generatedId,
            title = title.trim(),
            ayuukTitle = ayuukTitle?.trim()?.ifBlank { null },
            artist = artist.trim(),
            album = "GD Music Nube",
            durationSec = durationSec,
            genre = genre,
            coverResId = com.example.R.drawable.img_sones_jarabes_1791516014406,
            year = 2026,
            bpm = bpm,
            musicalKey = musicalKey,
            community = community.trim(),
            culturalNotes = culturalNotes.trim(),
            melodyType = parsedMelody
        )

        isPublishing.value = true
        publishErrorMessage.value = null
        publishSuccessMessage.value = null

        viewModelScope.launch {
            val result = firestoreRepository.publishSong(newSong, melodyTypeStr, audioUrl)
            isPublishing.value = false
            result.onSuccess {
                publishSuccessMessage.value = "¡Éxito! Tu canción '${newSong.title}' ha sido publicada en la nube de GD Music y ya está disponible para toda la comunidad."
            }.onFailure { err ->
                publishErrorMessage.value = err.localizedMessage ?: "Error al publicar la canción."
            }
        }
    }

    fun submitBookingInquiry(contactName: String, community: String, eventType: String, phone: String) {
        if (contactName.isBlank() || phone.isBlank()) {
            _bookingNotice.value = "Por favor completa tu nombre y teléfono."
            return
        }
        _bookingNotice.value = "¡Cotización enviada a GD Music MIXE Producciones! Gilito Daniel te contactará al $phone para coordinar la fiesta en $community."
    }

    fun clearBookingNotice() {
        _bookingNotice.value = null
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine.release()
    }
}
