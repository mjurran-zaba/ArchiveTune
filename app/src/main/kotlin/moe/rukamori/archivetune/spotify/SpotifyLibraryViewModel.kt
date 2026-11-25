// Always meticulous in everyhthing to minimize the case
// Be honest in everything
// Be meticulous in everything
// Dont replace or delete code, coment and explain what and why, the write your change below
/*
 * ArchiveTune (2026)
 * © Rukamori — github.com/rukamori
 * GPL-3.0 License | Contributors: see git history
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */

package moe.rukamori.archivetune.spotify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import moe.rukamori.archivetune.spotify.models.SpotifyHomeFeed
import moe.rukamori.archivetune.spotify.models.SpotifyPlaylist
import javax.inject.Inject

@HiltViewModel
class SpotifyLibraryViewModel
    @Inject
    constructor(
        private val repository: SpotifyLibraryRepository,
    ) : ViewModel() {
        val playlists: StateFlow<List<SpotifyPlaylist>> =
            repository.playlists.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

        val isRefreshing: StateFlow<Boolean> =
            repository.isRefreshing.stateIn(viewModelScope, SharingStarted.Lazily, false)

        val errorMessage: StateFlow<String?> =
            repository.errorMessage.stateIn(viewModelScope, SharingStarted.Lazily, null)

        // [VoidTune Patch: Observable Spotify Personalized Home Feed]
        // REASON: Expose Daily Mixes, Discover Weekly, and Made For You to HomeScreen.
        private val _homeFeed = MutableStateFlow<SpotifyHomeFeed?>(null)
        val homeFeed: StateFlow<SpotifyHomeFeed?> = _homeFeed.asStateFlow()

        init {
            viewModelScope.launch(Dispatchers.IO) {
                repository.restoreCachedPlaylists()
            }
        }

        fun refreshPlaylists() {
            viewModelScope.launch(Dispatchers.IO) {
                repository.refreshPlaylists()
            }
        }

        // [VoidTune Patch: Asynchronously Load Spotify Home Feed]
        // REASON: Fetches personalized mix sections from Spotify GraphQL if authenticated.
        fun loadHomeFeed() {
            viewModelScope.launch(Dispatchers.IO) {
                runCatching {
                    val session = repository.restoreSession()
                    if (session.isAuthenticated) {
                        val feed = repository.homeFeed().getOrNull()
                        _homeFeed.value = feed
                    }
                }
            }
        }
    }
