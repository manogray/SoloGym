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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sologym.model.AerobicExercise
import com.example.sologym.model.ExerciseType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseFormScreen(
    onNavigateBack: () -> Unit,
    viewModel: ExerciseFormViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var aerobicMenuExpanded by remember { mutableStateOf(false) }

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
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "QUAL É O TIPO DO EXERCÍCIO?",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            FilterChip(
                                selected = uiState.tipo == ExerciseType.STRENGTH,
                                onClick = { viewModel.onTipoChange(ExerciseType.STRENGTH) },
                                enabled = uiState.id == null,
                                label = { Text("FORÇA") },
                                modifier = Modifier.weight(1f),
                            )
                            FilterChip(
                                selected = uiState.tipo == ExerciseType.AEROBIC,
                                onClick = { viewModel.onTipoChange(ExerciseType.AEROBIC) },
                                enabled = uiState.id == null,
                                label = { Text("AERÓBICO") },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }

                if (uiState.tipo == ExerciseType.STRENGTH) {
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
                                Icon(Icons.Default.Delete, contentDescription = "REMOVER SÉRIE")
                            }
                        }
                    }

                    item {
                        Button(onClick = viewModel::addSerie) {
                            Text("ADICIONAR SÉRIE")
                        }
                    }

                    val strengthSubstitutes = uiState.availableSubstitutes.filter {
                        it.tipo == ExerciseType.STRENGTH
                    }
                    if (strengthSubstitutes.isNotEmpty()) {
                        item {
                            Text("SUBSTITUTOS", style = MaterialTheme.typography.titleMedium)
                        }
                        itemsIndexed(strengthSubstitutes) { _, substitute ->
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
                }

                if (uiState.tipo == ExerciseType.AEROBIC) {
                    item {
                        ExposedDropdownMenuBox(
                            expanded = aerobicMenuExpanded,
                            onExpandedChange = { aerobicMenuExpanded = it },
                        ) {
                            OutlinedTextField(
                                value = uiState.aerobicExercise?.displayName.orEmpty(),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("EXERCÍCIO AERÓBICO") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(aerobicMenuExpanded)
                                },
                                modifier = Modifier
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                    .fillMaxWidth(),
                            )
                            ExposedDropdownMenu(
                                expanded = aerobicMenuExpanded,
                                onDismissRequest = { aerobicMenuExpanded = false },
                            ) {
                                AerobicExercise.entries.forEach { exercise ->
                                    DropdownMenuItem(
                                        text = { Text(exercise.displayName) },
                                        onClick = {
                                            viewModel.onAerobicExerciseChange(exercise)
                                            aerobicMenuExpanded = false
                                        },
                                    )
                                }
                            }
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = uiState.duracaoMinutos,
                            onValueChange = viewModel::onDurationChange,
                            label = { Text("DURAÇÃO (MINUTOS)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        )
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
