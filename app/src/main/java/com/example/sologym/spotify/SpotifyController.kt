package com.example.sologym.spotify

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Base64
import com.example.sologym.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Singleton
class SpotifyController @Inject constructor(@ApplicationContext context: Context) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var pendingState: String? = null
    private var codeVerifier: String? = null
    private val _uiState = MutableStateFlow(
        SpotifyUiState(
            isConfigured = BuildConfig.SPOTIFY_CLIENT_ID.isNotBlank(),
            playlistInput = preferences.getString(KEY_PLAYLIST_URI, null).orEmpty(),
            playlistUri = preferences.getString(KEY_PLAYLIST_URI, null),
        )
    )
    val uiState: StateFlow<SpotifyUiState> = _uiState.asStateFlow()

    fun updatePlaylistInput(value: String) = _uiState.update { it.copy(playlistInput = value, message = null) }

    fun savePlaylist() {
        val normalized = SpotifyPlaylistUri.normalize(_uiState.value.playlistInput)
        if (normalized == null) return showMessage("Informe um link ou URI válido de playlist do Spotify.")
        preferences.edit().putString(KEY_PLAYLIST_URI, normalized).apply()
        _uiState.update { it.copy(playlistInput = normalized, playlistUri = normalized, message = "Playlist salva.") }
    }

    fun connect(activity: Activity) {
        if (BuildConfig.SPOTIFY_CLIENT_ID.isBlank()) return showMessage("Configure SPOTIFY_CLIENT_ID antes de compilar o aplicativo.")
        val token = preferences.getString(KEY_ACCESS_TOKEN, null)
        val expiresAt = preferences.getLong(KEY_EXPIRES_AT, 0L)
        if (!token.isNullOrBlank() && expiresAt > System.currentTimeMillis() + TOKEN_MARGIN_MS) return markConnected()
        val refresh = preferences.getString(KEY_REFRESH_TOKEN, null)
        if (!refresh.isNullOrBlank()) {
            _uiState.update { it.copy(isConnecting = true, message = null) }
            scope.launch { refreshAccessToken(refresh) }
        } else startAuthorization(activity)
    }

    fun handleAuthorizationIntent(activity: Activity, intent: Intent) {
        val data = intent.data ?: return
        if (data.scheme != REDIRECT_SCHEME || data.host != REDIRECT_HOST) return
        if (data.getQueryParameter("state") != pendingState) return showMessage("Resposta de autorização inválida.")
        data.getQueryParameter("error")?.let { return showMessage("Autorização cancelada: $it") }
        val code = data.getQueryParameter("code")
        val verifier = codeVerifier
        if (code == null || verifier == null) return showMessage("O Spotify não retornou um código de autorização.")
        _uiState.update { it.copy(isConnecting = true, message = null) }
        scope.launch { exchangeCode(code, verifier) }
    }

    fun disconnect() = _uiState.update { it.copy(isConnected = false, isPaused = true, trackName = null, artistName = null) }
    fun clearMessage() = _uiState.update { it.copy(message = null) }

    fun playPlaylist() {
        val uri = _uiState.value.playlistUri ?: return showMessage("Salve uma playlist na tela Informações.")
        apiCall("PUT", "/v1/me/player/play", "{\"context_uri\":\"$uri\"}")
    }
    fun togglePlayback() = apiCall("PUT", if (_uiState.value.isPaused) "/v1/me/player/play" else "/v1/me/player/pause")
    fun skipPrevious() = apiCall("POST", "/v1/me/player/previous")
    fun skipNext() = apiCall("POST", "/v1/me/player/next")

    private fun startAuthorization(activity: Activity) {
        val verifier = randomVerifier()
        val state = UUID.randomUUID().toString()
        codeVerifier = verifier
        pendingState = state
        _uiState.update { it.copy(isConnecting = true, message = null) }
        val uri = Uri.parse("https://accounts.spotify.com/authorize").buildUpon()
            .appendQueryParameter("client_id", BuildConfig.SPOTIFY_CLIENT_ID)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("redirect_uri", BuildConfig.SPOTIFY_REDIRECT_URI)
            .appendQueryParameter("scope", SCOPES)
            .appendQueryParameter("state", state)
            .appendQueryParameter("code_challenge_method", "S256")
            .appendQueryParameter("code_challenge", challenge(verifier)).build()
        activity.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    private suspend fun exchangeCode(code: String, verifier: String) {
        runCatching { tokenRequest(mapOf("grant_type" to "authorization_code", "code" to code, "redirect_uri" to BuildConfig.SPOTIFY_REDIRECT_URI, "client_id" to BuildConfig.SPOTIFY_CLIENT_ID, "code_verifier" to verifier)) }
            .onSuccess { saveToken(it); markConnected() }
            .onFailure { showMessage(it.message ?: "Não foi possível autenticar no Spotify.") }
    }

    private suspend fun refreshAccessToken(refresh: String) {
        runCatching { tokenRequest(mapOf("grant_type" to "refresh_token", "refresh_token" to refresh, "client_id" to BuildConfig.SPOTIFY_CLIENT_ID)) }
            .onSuccess { saveToken(it); markConnected() }
            .onFailure { preferences.edit().remove(KEY_ACCESS_TOKEN).remove(KEY_REFRESH_TOKEN).apply(); showMessage("A autorização expirou. Conecte novamente.") }
    }

    private fun saveToken(json: String) {
        val access = json.jsonValue("access_token") ?: error("Spotify não retornou access token")
        val expires = json.jsonValue("expires_in")?.toLongOrNull() ?: 3600L
        val refresh = json.jsonValue("refresh_token")
        preferences.edit().putString(KEY_ACCESS_TOKEN, access).putLong(KEY_EXPIRES_AT, System.currentTimeMillis() + expires * 1000).apply { if (refresh != null) putString(KEY_REFRESH_TOKEN, refresh) }.apply()
    }
    private fun markConnected() = _uiState.update { it.copy(isConnecting = false, isConnected = true, message = "Spotify conectado.") }

    private fun apiCall(method: String, path: String, body: String? = null) {
        val token = preferences.getString(KEY_ACCESS_TOKEN, null) ?: return showMessage("Conecte o Spotify primeiro.")
        scope.launch(Dispatchers.IO) {
            runCatching {
                val connection = URL("https://api.spotify.com$path").openConnection() as HttpURLConnection
                connection.requestMethod = method
                connection.setRequestProperty("Authorization", "Bearer $token")
                connection.setRequestProperty("Content-Type", "application/json")
                if (body != null) { connection.doOutput = true; connection.outputStream.use { it.write(body.toByteArray()) } }
                if (connection.responseCode !in 200..299) error("Spotify recusou o comando (HTTP ${connection.responseCode}). O controle exige Premium.")
            }.onFailure { showMessage(it.message ?: "Não foi possível executar o comando no Spotify.") }
        }
    }

    private suspend fun tokenRequest(values: Map<String, String>): String = withContext(Dispatchers.IO) {
        val connection = URL("https://accounts.spotify.com/api/token").openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
        connection.outputStream.use { it.write(values.entries.joinToString("&") { "${enc(it.key)}=${enc(it.value)}" }.toByteArray()) }
        val responseCode = connection.responseCode
        val stream = if (responseCode in 200..299) connection.inputStream else connection.errorStream
        val response = BufferedReader(InputStreamReader(stream)).use { it.readText() }
        if (responseCode !in 200..299) error("Falha na autorização: ${response.jsonValue("error_description") ?: response}")
        response
    }

    private fun showMessage(message: String) = _uiState.update { it.copy(isConnecting = false, message = message) }
    private fun enc(value: String) = URLEncoder.encode(value, Charsets.UTF_8.name())
    private fun randomVerifier() = ByteArray(64).also { SecureRandom().nextBytes(it) }.let { Base64.encodeToString(it, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING) }
    private fun challenge(value: String) = Base64.encodeToString(MessageDigest.getInstance("SHA-256").digest(value.toByteArray()), Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    private fun String.jsonValue(key: String): String? = Regex("\"$key\"\\s*:\\s*\"([^\"]*)\"").find(this)?.groupValues?.get(1) ?: Regex("\"$key\"\\s*:\\s*(\\d+)").find(this)?.groupValues?.get(1)

    private companion object {
        const val PREFERENCES_NAME = "spotify_preferences"
        const val KEY_PLAYLIST_URI = "playlist_uri"
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
        const val KEY_EXPIRES_AT = "expires_at"
        const val REDIRECT_SCHEME = "sologym"
        const val REDIRECT_HOST = "spotify-callback"
        const val SCOPES = "user-read-playback-state user-modify-playback-state"
        const val TOKEN_MARGIN_MS = 60_000L
    }
}
