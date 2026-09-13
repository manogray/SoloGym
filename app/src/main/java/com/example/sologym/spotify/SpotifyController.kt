package com.example.sologym.spotify

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.example.sologym.BuildConfig
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.client.Subscription
import com.spotify.protocol.types.PlayerState
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Singleton
class SpotifyController @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private var appRemote: SpotifyAppRemote? = null
    private var playerStateSubscription: Subscription<PlayerState>? = null

    private val savedPlaylistUri = preferences.getString(KEY_PLAYLIST_URI, null)
    private val _uiState = MutableStateFlow(
        SpotifyUiState(
            isConfigured = BuildConfig.SPOTIFY_CLIENT_ID.isNotBlank(),
            playlistInput = savedPlaylistUri.orEmpty(),
            playlistUri = savedPlaylistUri,
        )
    )
    val uiState: StateFlow<SpotifyUiState> = _uiState.asStateFlow()

    fun updatePlaylistInput(value: String) {
        _uiState.update { it.copy(playlistInput = value, message = null) }
    }

    fun savePlaylist() {
        val normalized = SpotifyPlaylistUri.normalize(_uiState.value.playlistInput)
        if (normalized == null) {
            _uiState.update {
                it.copy(message = "Informe um link ou URI válido de playlist do Spotify.")
            }
            return
        }
        preferences.edit().putString(KEY_PLAYLIST_URI, normalized).apply()
        _uiState.update {
            it.copy(
                playlistInput = normalized,
                playlistUri = normalized,
                message = "Playlist salva.",
            )
        }
    }

    fun connect(activity: Activity) {
        if (BuildConfig.SPOTIFY_CLIENT_ID.isBlank()) {
            _uiState.update {
                it.copy(message = "Configure SPOTIFY_CLIENT_ID antes de compilar o aplicativo.")
            }
            return
        }
        if (appRemote?.isConnected == true || _uiState.value.isConnecting) return

        _uiState.update { it.copy(isConnecting = true, message = null) }
        val request = AuthorizationRequest.Builder(
            BuildConfig.SPOTIFY_CLIENT_ID,
            AuthorizationResponse.Type.CODE,
            BuildConfig.SPOTIFY_REDIRECT_URI,
        )
            .setScopes(arrayOf(APP_REMOTE_CONTROL_SCOPE))
            .setShowDialog(true)
            .build()
        AuthorizationClient.openLoginActivity(activity, AUTH_REQUEST_CODE, request)
    }

    fun handleAuthorizationResult(activity: Activity, resultCode: Int, data: Intent?) {
        val response = AuthorizationClient.getResponse(resultCode, data)
        when (response.type) {
            AuthorizationResponse.Type.CODE,
            AuthorizationResponse.Type.TOKEN -> connectRemote(activity)
            AuthorizationResponse.Type.ERROR -> _uiState.update {
                it.copy(
                    isConnecting = false,
                    message = response.error ?: "O Spotify recusou a autorização.",
                )
            }
            else -> _uiState.update {
                it.copy(isConnecting = false, message = "Autorização do Spotify cancelada.")
            }
        }
    }

    private fun connectRemote(activity: Activity) {
        val params = ConnectionParams.Builder(BuildConfig.SPOTIFY_CLIENT_ID)
            .setRedirectUri(BuildConfig.SPOTIFY_REDIRECT_URI)
            .showAuthView(false)
            .build()
        SpotifyAppRemote.connect(activity, params, object : Connector.ConnectionListener {
            override fun onConnected(remote: SpotifyAppRemote) {
                appRemote = remote
                _uiState.update {
                    it.copy(isConnecting = false, isConnected = true, message = "Spotify conectado.")
                }
                subscribeToPlayerState(remote)
            }

            override fun onFailure(error: Throwable) {
                _uiState.update {
                    it.copy(
                        isConnecting = false,
                        isConnected = false,
                        message = error.message ?: "Não foi possível conectar ao Spotify.",
                    )
                }
            }
        })
    }

    fun disconnect() {
        playerStateSubscription?.cancel()
        playerStateSubscription = null
        appRemote?.let(SpotifyAppRemote::disconnect)
        appRemote = null
        _uiState.update {
            it.copy(
                isConnecting = false,
                isConnected = false,
                isPaused = true,
                trackName = null,
                artistName = null,
            )
        }
    }

    fun playPlaylist() {
        val remote = connectedRemote() ?: return
        val uri = _uiState.value.playlistUri
        if (uri == null) {
            _uiState.update { it.copy(message = "Salve uma playlist na tela Informações.") }
            return
        }
        remote.playerApi.play(uri).setErrorCallback(::showError)
    }

    fun togglePlayback() {
        val remote = connectedRemote() ?: return
        val result = if (_uiState.value.isPaused) remote.playerApi.resume() else remote.playerApi.pause()
        result.setErrorCallback(::showError)
    }

    fun skipPrevious() {
        connectedRemote()?.playerApi?.skipPrevious()?.setErrorCallback(::showError)
    }

    fun skipNext() {
        connectedRemote()?.playerApi?.skipNext()?.setErrorCallback(::showError)
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    private fun subscribeToPlayerState(remote: SpotifyAppRemote) {
        playerStateSubscription?.cancel()
        val subscription = remote.playerApi.subscribeToPlayerState()
        playerStateSubscription = subscription
        subscription.setEventCallback { playerState ->
            _uiState.update {
                it.copy(
                    isPaused = playerState.isPaused,
                    trackName = playerState.track?.name,
                    artistName = playerState.track?.artist?.name,
                )
            }
        }
        subscription.setErrorCallback(::showError)
    }

    private fun connectedRemote(): SpotifyAppRemote? {
        val remote = appRemote
        if (remote?.isConnected != true) {
            _uiState.update { it.copy(message = "Conecte o Spotify primeiro.") }
            return null
        }
        return remote
    }

    private fun showError(error: Throwable) {
        _uiState.update {
            it.copy(message = error.message ?: "O Spotify não conseguiu executar esta ação.")
        }
    }

    companion object {
        const val AUTH_REQUEST_CODE = 1337
        private const val PREFERENCES_NAME = "spotify_preferences"
        private const val KEY_PLAYLIST_URI = "playlist_uri"
        private const val APP_REMOTE_CONTROL_SCOPE = "app-remote-control"
    }
}
