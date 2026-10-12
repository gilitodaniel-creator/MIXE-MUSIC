package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.PlaylistPlay
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.credentials.CredentialManager
import androidx.lifecycle.lifecycleScope
import com.example.auth.AuthManager
import com.example.ui.components.BandDetailBottomSheet
import com.example.ui.components.MiniPlayer
import com.example.ui.components.ProfessionalEqualizerModal
import com.example.ui.components.SongDetailBottomSheet
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CatalogScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PlayerScreen
import com.example.ui.screens.PlaylistsScreen
import com.example.ui.screens.ProduccionesScreen
import com.example.ui.screens.PublishSongScreen
import com.example.ui.screens.SoundboardScreen
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldBrassContainer
import com.example.ui.theme.GoldBrassPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextTertiary
import com.example.ui.viewmodel.MusicViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {

    private val viewModel: MusicViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }

                DisposableEffect(Unit) {
                    val listener = FirebaseAuth.AuthStateListener { auth ->
                        currentUser = auth.currentUser
                    }
                    Firebase.auth.addAuthStateListener(listener)
                    onDispose {
                        Firebase.auth.removeAuthStateListener(listener)
                    }
                }

                LaunchedEffect(Unit) {
                    val credentialManager = CredentialManager.create(this@MainActivity)
                    AuthManager.attemptAutoSignIn(
                        context = this@MainActivity,
                        credentialManager = credentialManager,
                        onAuthSuccess = { currentUser = Firebase.auth.currentUser },
                        onUnauthenticated = {},
                        scope = lifecycleScope
                    )
                }

                if (currentUser == null) {
                    AuthScreen(
                        onAuthSuccess = { currentUser = Firebase.auth.currentUser }
                    )
                } else {
                    MainContent(
                        viewModel = viewModel,
                        onSignOut = {
                            val credentialManager = CredentialManager.create(this@MainActivity)
                            AuthManager.signOut(
                                context = this@MainActivity,
                                credentialManager = credentialManager,
                                onSignOutComplete = { currentUser = null },
                                scope = lifecycleScope
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MainContent(
    viewModel: MusicViewModel,
    onSignOut: () -> Unit
) {
    val playerState by viewModel.playerState.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val activeGenre by viewModel.activeGenre.collectAsState()
    val favoriteIds by viewModel.favoriteSongIds.collectAsState()
    val playlists by viewModel.playlists.collectAsState()
    val historySongs by viewModel.recentHistorySongs.collectAsState()
    val selectedBand by viewModel.selectedBand.collectAsState()
    val selectedSongForDetails by viewModel.selectedSongForDetails.collectAsState()
    val bookingNotice by viewModel.bookingNotice.collectAsState()
    val showPublisherScreen by viewModel.showPublisherScreen.collectAsState()
    val showEqualizerDialog by viewModel.showEqualizerDialog.collectAsState()

    // Handle back button
    BackHandler(
        enabled = showEqualizerDialog ||
                showPublisherScreen ||
                playerState.isExpandedPlayer ||
                currentTab != 0 ||
                selectedBand != null ||
                selectedSongForDetails != null
    ) {
        when {
            showEqualizerDialog -> viewModel.setShowEqualizerDialog(false)
            showPublisherScreen -> viewModel.setShowPublisherScreen(false)
            playerState.isExpandedPlayer -> viewModel.setExpandedPlayer(false)
            selectedBand != null -> viewModel.selectBand(null)
            selectedSongForDetails != null -> viewModel.selectSongForDetails(null)
            currentTab != 0 -> viewModel.setTab(0)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .testTag("main_scaffold"),
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    // Floating MiniPlayer docked above the bottom nav
                    MiniPlayer(
                        playerState = playerState,
                        onTogglePlay = { viewModel.togglePlayPause() },
                        onSkipNext = { viewModel.playNext() },
                        onOpenFullPlayer = { viewModel.setExpandedPlayer(true) }
                    )

                    // Material 3 Navigation Bar
                    NavigationBar(
                        containerColor = DarkSurface,
                        contentColor = TextPrimary
                    ) {
                        NavigationBarItem(
                            selected = currentTab == 0,
                            onClick = { viewModel.setTab(0) },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == 0) Icons.Default.Home else Icons.Outlined.Home,
                                    contentDescription = "Inicio"
                                )
                            },
                            label = { Text("Inicio") },
                            colors = navBarItemColors(),
                            modifier = Modifier.testTag("nav_home")
                        )

                        NavigationBarItem(
                            selected = currentTab == 1,
                            onClick = { viewModel.setTab(1) },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == 1) Icons.Default.LibraryMusic else Icons.Outlined.LibraryMusic,
                                    contentDescription = "Catálogo"
                                )
                            },
                            label = { Text("Catálogo") },
                            colors = navBarItemColors(),
                            modifier = Modifier.testTag("nav_catalog")
                        )

                        NavigationBarItem(
                            selected = currentTab == 2,
                            onClick = { viewModel.setTab(2) },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == 2) Icons.Default.GraphicEq else Icons.Outlined.GraphicEq,
                                    contentDescription = "Estudio"
                                )
                            },
                            label = { Text("Estudio") },
                            colors = navBarItemColors(),
                            modifier = Modifier.testTag("nav_soundboard")
                        )

                        NavigationBarItem(
                            selected = currentTab == 3,
                            onClick = { viewModel.setTab(3) },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == 3) Icons.Default.PlaylistPlay else Icons.Outlined.PlaylistPlay,
                                    contentDescription = "Listas"
                                )
                            },
                            label = { Text("Listas") },
                            colors = navBarItemColors(),
                            modifier = Modifier.testTag("nav_playlists")
                        )

                        NavigationBarItem(
                            selected = currentTab == 4,
                            onClick = { viewModel.setTab(4) },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == 4) Icons.Default.Album else Icons.Outlined.Album,
                                    contentDescription = "Producciones"
                                )
                            },
                            label = { Text("GD Music") },
                            colors = navBarItemColors(),
                            modifier = Modifier.testTag("nav_producciones")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    0 -> HomeScreen(
                        viewModel = viewModel,
                        favoriteIds = favoriteIds,
                        onNavigateCatalog = { viewModel.setTab(1) },
                        onNavigateSoundboard = { viewModel.setTab(2) },
                        onNavigateProducciones = { viewModel.setTab(4) },
                        onNavigatePublisher = { viewModel.setShowPublisherScreen(true) }
                    )
                    1 -> CatalogScreen(
                        viewModel = viewModel,
                        searchQuery = searchQuery,
                        activeGenre = activeGenre,
                        favoriteIds = favoriteIds,
                        playlists = playlists,
                        currentSongId = playerState.currentSong.id,
                        isPlaying = playerState.isPlaying
                    )
                    2 -> SoundboardScreen(viewModel = viewModel)
                    3 -> PlaylistsScreen(
                        viewModel = viewModel,
                        favoriteIds = favoriteIds,
                        playlists = playlists,
                        historySongs = historySongs
                    )
                    4 -> ProduccionesScreen(
                        viewModel = viewModel,
                        bookingNotice = bookingNotice,
                        onNavigatePublisher = { viewModel.setShowPublisherScreen(true) },
                        onSignOut = onSignOut
                    )
                }
            }
        }

        // Full Screen Player Modal with Slide-in animation
        AnimatedVisibility(
            visible = playerState.isExpandedPlayer,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            PlayerScreen(
                viewModel = viewModel,
                isFavorite = favoriteIds.contains(playerState.currentSong.id),
                onClose = { viewModel.setExpandedPlayer(false) }
            )
        }

        // Publish Song Screen Modal
        AnimatedVisibility(
            visible = showPublisherScreen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            PublishSongScreen(
                viewModel = viewModel,
                onBack = { viewModel.setShowPublisherScreen(false) }
            )
        }

        // Band Detail Modal Sheet
        selectedBand?.let { band ->
            BandDetailBottomSheet(
                band = band,
                onDismiss = { viewModel.selectBand(null) },
                onPlayFeaturedSong = {
                    val song = viewModel.allSongs.value.find { it.id == band.featuredSongId }
                        ?: viewModel.allSongs.value.first()
                    viewModel.playSong(song)
                }
            )
        }

        // Song Cultural Detail Modal Sheet
        selectedSongForDetails?.let { song ->
            SongDetailBottomSheet(
                song = song,
                onDismiss = { viewModel.selectSongForDetails(null) },
                onPlay = { viewModel.playSong(song) }
            )
        }

        // Professional Equalizer DSP Modal Sheet
        if (showEqualizerDialog) {
            ProfessionalEqualizerModal(
                viewModel = viewModel,
                onDismiss = { viewModel.setShowEqualizerDialog(false) }
            )
        }
    }
}

@Composable
private fun navBarItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = GoldBrassPrimary,
    selectedTextColor = GoldBrassPrimary,
    indicatorColor = GoldBrassContainer,
    unselectedIconColor = TextTertiary,
    unselectedTextColor = TextTertiary
)
