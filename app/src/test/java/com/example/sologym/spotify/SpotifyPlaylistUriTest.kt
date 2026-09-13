package com.example.sologym.spotify

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SpotifyPlaylistUriTest {
    @Test
    fun `normalizes Spotify playlist URI`() {
        assertEquals(
            "spotify:playlist:37i9dQZF1DX70RN3TfWWJh",
            SpotifyPlaylistUri.normalize("spotify:playlist:37i9dQZF1DX70RN3TfWWJh"),
        )
    }

    @Test
    fun `normalizes shared playlist link and ignores query parameters`() {
        assertEquals(
            "spotify:playlist:37i9dQZF1DX70RN3TfWWJh",
            SpotifyPlaylistUri.normalize(
                "https://open.spotify.com/playlist/37i9dQZF1DX70RN3TfWWJh?si=abc123"
            ),
        )
    }

    @Test
    fun `rejects non-playlist links`() {
        assertNull(SpotifyPlaylistUri.normalize("https://open.spotify.com/track/abc123"))
        assertNull(SpotifyPlaylistUri.normalize("spotify:album:abc123"))
        assertNull(SpotifyPlaylistUri.normalize(""))
    }
}
