package com.example.sologym.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sologym.spotify.SpotifyUiState

@Composable
fun SpotifyPlayerCard(
    state: SpotifyUiState,
    onConnect: () -> Unit,
    onPlayPlaylist: () -> Unit,
    onTogglePlayback: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("SPOTIFY", style = MaterialTheme.typography.titleMedium)
            when {
                !state.isConfigured -> Text(
                    "INTEGRAÇÃO NÃO CONFIGURADA NESTE APK.",
                    style = MaterialTheme.typography.bodySmall,
                )
                !state.isConnected -> Button(
                    onClick = onConnect,
                    enabled = !state.isConnecting,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(if (state.isConnecting) "CONECTANDO..." else "CONECTAR SPOTIFY")
                }
                else -> {
                    Text(
                        state.trackName ?: "NENHUMA MÚSICA EM REPRODUÇÃO",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    state.artistName?.let { artist ->
                        Text(artist, style = MaterialTheme.typography.bodySmall)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(onClick = onPrevious) {
                            Icon(Icons.Default.SkipPrevious, contentDescription = "Música anterior")
                        }
                        IconButton(onClick = onTogglePlayback) {
                            Icon(
                                if (state.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                contentDescription = if (state.isPaused) "Continuar" else "Pausar",
                            )
                        }
                        IconButton(onClick = onNext) {
                            Icon(Icons.Default.SkipNext, contentDescription = "Próxima música")
                        }
                    }
                    Button(
                        onClick = onPlayPlaylist,
                        enabled = state.playlistUri != null,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("TOCAR PLAYLIST DE TREINO")
                    }
                }
            }
            state.message?.let { message ->
                Text(message, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
