package com.example.sologym.spotify

data class SpotifyUiState(
    val isConfigured: Boolean = false,
    val isConnecting: Boolean = false,
    val isConnected: Boolean = false,
    val isPaused: Boolean = true,
    val trackName: String? = null,
    val artistName: String? = null,
    val playlistInput: String = "",
    val playlistUri: String? = null,
    val message: String? = null,
)
