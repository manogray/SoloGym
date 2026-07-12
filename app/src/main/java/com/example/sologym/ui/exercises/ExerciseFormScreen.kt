package com.example.sologym.ui.exercises

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseFormScreen(
    onNavigateBack: () -> Unit,
    viewModel: ExerciseFormViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.id == null) "NOVO EXERCÍCIO" else "EDITAR EXERCÍCIO") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = uiState.nome,
                        onValueChange = viewModel::onNomeChange,
                        label = { Text("NOME DO EXERCÍCIO") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = uiState.descansoSegundos,
                        onValueChange = viewModel::onDescansoChange,
                        label = { Text("DESCANSO (SEGUNDOS)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                item {
                    Text("SÉRIES", style = MaterialTheme.typography.titleMedium)
                }

                itemsIndexed(uiState.series) { index, serie ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("SÉRIE ${serie.ordem}", modifier = Modifier.width(60.dp))
                        OutlinedTextField(
                            value = serie.repeticoes,
                            onValueChange = { viewModel.onSerieRepsChange(index, it) },
                            label = { Text("REPS") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = serie.carga,
                            onValueChange = { viewModel.onSerieCargaChange(index, it) },
                            label = { Text("CARGA (KG)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                        IconButton(onClick = { viewModel.removeSerie(index) }) {
                            Icon(Icons.Default.Delete, contentDescription = "REMOVE SERIES")
                        }
                    }
                }

                item {
                    Button(onClick = viewModel::addSerie) {
                        Text("ADICIONAR SÉRIE")
                    }
                }

                if (uiState.availableSubstitutes.isNotEmpty()) {
                    item {
                        Text("SUBSTITUTOS", style = MaterialTheme.typography.titleMedium)
                    }
                    itemsIndexed(uiState.availableSubstitutes) { _, substitute ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = uiState.selectedSubstitutesIds.contains(substitute.id),
                                onCheckedChange = { viewModel.toggleSubstitute(substitute.id) }
                            )
                            Text(substitute.nome)
                        }
                    }
                }

                item {
                    if (uiState.error != null) {
                        Text(uiState.error!!, color = MaterialTheme.colorScheme.error)
                    }
                    Button(
                        onClick = viewModel::save,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("SALVAR")
                    }
                }
            }
        }
    }
}
