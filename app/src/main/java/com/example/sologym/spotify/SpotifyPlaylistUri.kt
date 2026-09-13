package com.example.sologym.spotify

internal object SpotifyPlaylistUri {
    private val playlistLink = Regex("https?://open\\.spotify\\.com/playlist/([A-Za-z0-9]+)")
    private val playlistId = Regex("[A-Za-z0-9]+")

    fun normalize(value: String): String? {
        val trimmed = value.trim()
        val spotifyUriId = trimmed.takeIf { it.startsWith("spotify:playlist:") }
            ?.substringAfterLast(':')
        val webId = playlistLink.find(trimmed)?.groupValues?.getOrNull(1)
        val id = spotifyUriId ?: webId
        return id?.takeIf { it.matches(playlistId) }?.let { "spotify:playlist:$it" }
    }
}
