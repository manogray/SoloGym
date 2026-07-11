package com.example.sologym.ui.workouts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutFormScreen(
    onNavigateBack: () -> Unit,
    viewModel: WorkoutFormViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDayPicker by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
    }

    if (uiState.showExerciseSelection) {
        AlertDialog(
            onDismissRequest = viewModel::toggleExerciseSelection,
            title = { Text("SELECIONAR EXERCÍCIO") },
            text = {
                LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                    itemsIndexed(uiState.availableExercises) { _, exercise ->
                        ListItem(
                            headlineContent = { Text(exercise.nome) },
                            modifier = Modifier.fillMaxWidth(),
                            trailingContent = {
                                Button(onClick = { 
                                    viewModel.addExercise(exercise)
                                    viewModel.toggleExerciseSelection()
                                }) {
                                    Text("ADICIONAR")
                                }
                            }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = viewModel::toggleExerciseSelection) {
                    Text("FECHAR")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.id == null) "NOVO TREINO" else "EDITAR TREINO") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Day Selector
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = { showDayPicker = true }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = uiState.diaSemana?.getDisplayName(TextStyle.FULL, Locale.getDefault())?.uppercase()
                            ?: "SELECIONE O DIA DA SEMANA",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (showDayPicker) {
                val days = DayOfWeek.entries
                ModalBottomSheet(onDismissRequest = { showDayPicker = false }) {
                    LazyColumn(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        itemsIndexed(days) { _, day ->
                            ListItem(
                                headlineContent = { Text(day.getDisplayName(TextStyle.FULL, Locale.getDefault()).uppercase()) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.onDaySelected(day)
                                        showDayPicker = false
                                    }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("EXERCÍCIOS", style = MaterialTheme.typography.titleLarge)
                Button(onClick = viewModel::toggleExerciseSelection) {
                    Text("ADICIONAR")
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                itemsIndexed(uiState.selectedExercises) { index, exercise ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${index + 1}.", modifier = Modifier.width(24.dp))
                            Text(exercise.nome, modifier = Modifier.weight(1f))
                            
                            IconButton(onClick = { viewModel.moveExerciseUp(index) }) {
                                Icon(Icons.Default.ArrowUpward, contentDescription = "Subir")
                            }
                            IconButton(onClick = { viewModel.moveExerciseDown(index) }) {
                                Icon(Icons.Default.ArrowDownward, contentDescription = "Descer")
                            }
                            IconButton(onClick = { viewModel.removeExercise(index) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remover", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }

            if (uiState.error != null) {
                Text(uiState.error!!, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = viewModel::save,
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            ) {
                Text("SALVAR")
            }
        }
    }
}
