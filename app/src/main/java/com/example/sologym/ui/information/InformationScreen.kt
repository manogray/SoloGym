package com.example.sologym.ui.information

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sologym.ui.components.ProfilePhoto
import com.example.sologym.ui.components.SoloTopBar
import com.example.sologym.model.ProfileImages
import com.example.sologym.spotify.SpotifyViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformationScreen(
    onOpenDrawer: () -> Unit,
    viewModel: InformationViewModel = hiltViewModel(),
    spotifyViewModel: SpotifyViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spotifyState by spotifyViewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalContext.current.findActivity()
    var showDatePicker by remember { mutableStateOf(false) }
    var showPhotoPicker by remember { mutableStateOf(false) }

    if (showPhotoPicker) {
        ModalBottomSheet(onDismissRequest = { showPhotoPicker = false }) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text("ESCOLHA SUA FOTO", style = MaterialTheme.typography.titleLarge)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxWidth().height(380.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(ProfileImages.selectable) { profileImage ->
                        OutlinedButton(
                            onClick = {
                                viewModel.onPhotoChange(profileImage.name)
                                showPhotoPicker = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                ProfilePhoto(photoUri = profileImage.name, size = 104.dp)
                                Text(profileImage.name.substringBefore("_profile").uppercase())
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val initialDateMillis = uiState.dataNascimento
            ?.atStartOfDay(ZoneOffset.UTC)
            ?.toInstant()
            ?.toEpochMilli()
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialDateMillis,
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            viewModel.onBirthDateChange(
                                Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                            )
                        }
                        showDatePicker = false
                    },
                ) {
                    Text("CONFIRMAR")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("CANCELAR")
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            SoloTopBar(
                imageRes = com.example.sologym.R.drawable.info,
                onMenuClick = onOpenDrawer,
            )
        },
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                ProfilePhoto(photoUri = uiState.fotoUri)
                OutlinedButton(
                    onClick = { showPhotoPicker = true },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("ALTERAR FOTO")
                }
                OutlinedTextField(
                    value = uiState.nome,
                    onValueChange = viewModel::onNameChange,
                    label = { Text("NOME") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        uiState.dataNascimento?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            ?: "DATA DE NASCIMENTO"
                    )
                }
                if (uiState.currentWeightKg != null && uiState.currentHeightCm != null) {
                    Text(
                        text = "MEDIDAS ATUAIS: " +
                            "${formatMeasurement(requireNotNull(uiState.currentWeightKg))} KG • " +
                            "${formatMeasurement(requireNotNull(uiState.currentHeightCm))} CM",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Text(
                        text = "PREENCHA OS DOIS CAMPOS ABAIXO PARA ADICIONAR UMA NOVA MEDIÇÃO.",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                OutlinedTextField(
                    value = uiState.pesoKg,
                    onValueChange = viewModel::onWeightChange,
                    label = {
                        Text(
                            if (uiState.currentWeightKg == null) "PESO ATUAL (KG)"
                            else "NOVO PESO (KG)"
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = uiState.alturaCm,
                    onValueChange = viewModel::onHeightChange,
                    label = {
                        Text(
                            if (uiState.currentHeightCm == null) "ALTURA ATUAL (CM)"
                            else "NOVA ALTURA (CM)"
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                )
                uiState.error?.let { error ->
                    Text(error, color = MaterialTheme.colorScheme.error)
                }
                if (uiState.saved) {
                    Text("INFORMAÇÕES SALVAS", color = MaterialTheme.colorScheme.primary)
                }
                Button(
                    onClick = viewModel::save,
                    enabled = !uiState.isSaving,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator()
                    } else {
                        Text("SALVAR")
                    }
                }
                Text(
                    "SPOTIFY",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    "COLE O LINK DE UMA PLAYLIST PARA USÁ-LA DURANTE OS TREINOS.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = spotifyState.playlistInput,
                    onValueChange = spotifyViewModel::updatePlaylistInput,
                    label = { Text("LINK OU URI DA PLAYLIST") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedButton(
                    onClick = spotifyViewModel::savePlaylist,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("SALVAR PLAYLIST")
                }
                Button(
                    onClick = {
                        if (spotifyState.isConnected) {
                            spotifyViewModel.disconnect()
                        } else {
                            activity?.let(spotifyViewModel::connect)
                        }
                    },
                    enabled = spotifyState.isConfigured &&
                        !spotifyState.isConnecting &&
                        activity != null,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        when {
                            spotifyState.isConnected -> "DESCONECTAR SPOTIFY"
                            spotifyState.isConnecting -> "CONECTANDO..."
                            else -> "CONECTAR SPOTIFY"
                        }
                    )
                }
                if (!spotifyState.isConfigured) {
                    Text(
                        "CONFIGURE SPOTIFY_CLIENT_ID E GERE O APK NOVAMENTE.",
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                spotifyState.message?.let { message -> Text(message) }
            }
        }
    }
}

private fun formatMeasurement(value: Double): String =
    if (value % 1.0 == 0.0) value.toInt().toString() else value.toString()

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
