package com.example.sologym.spotify

import android.app.Activity
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SpotifyViewModel @Inject constructor(
    private val controller: SpotifyController,
) : ViewModel() {
    val uiState = controller.uiState

    fun updatePlaylistInput(value: String) = controller.updatePlaylistInput(value)
    fun savePlaylist() = controller.savePlaylist()
    fun connect(activity: Activity) = controller.connect(activity)
    fun disconnect() = controller.disconnect()
    fun playPlaylist() = controller.playPlaylist()
    fun togglePlayback() = controller.togglePlayback()
    fun skipPrevious() = controller.skipPrevious()
    fun skipNext() = controller.skipNext()
    fun clearMessage() = controller.clearMessage()
}
