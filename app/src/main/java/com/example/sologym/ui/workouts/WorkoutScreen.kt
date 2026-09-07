package com.example.sologym.ui.workouts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sologym.ui.components.EmptyState
import com.example.sologym.ui.components.WorkoutCard
import com.example.sologym.ui.components.SoloTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen(
    onOpenDrawer: () -> Unit,
    onAddWorkout: () -> Unit,
    onEditWorkout: (Long) -> Unit,
    viewModel: WorkoutViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            SoloTopBar(
                "TREINOS",
                Icons.AutoMirrored.Outlined.Assignment,
                onMenuClick = onOpenDrawer,
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddWorkout) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar")
            }
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        } else if (uiState.workouts.isEmpty()) {
            EmptyState(
                icon = Icons.AutoMirrored.Filled.EventNote,
                message = "REGISTRE SEUS TREINOS E COMECE SUA TRANSFORMAÇÃO",
                modifier = Modifier.padding(padding),
            )
        } else {
            if (uiState.workoutToDelete != null) {
                AlertDialog(
                    onDismissRequest = viewModel::onDismissDeleteDialog,
                    title = { Text("EXCLUIR TREINO ?") },
                    text = { Text("O TREINO DE ${uiState.workoutToDelete?.diaSemana?.name} SERÁ EXCLUÍDO. ESSA AÇÃO É IRREVERSÍVEL.") },
                    confirmButton = {
                        TextButton(
                            onClick = { viewModel.deleteWorkout(uiState.workoutToDelete!!) },
                        ) {
                            Text("CONFIRMAR", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = viewModel::onDismissDeleteDialog) {
                            Text("CANCELAR")
                        }
                    },
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
            ) {
                items(uiState.workouts) { workout ->
                    WorkoutCard(
                        treino = workout,
                        onClick = { onEditWorkout(workout.id) },
                    ) {
                        viewModel.onDeleteClick(workout)
                    }
                }
            }
        }
    }
}
