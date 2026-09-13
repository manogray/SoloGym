package com.example.sologym.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sologym.ui.components.ExerciseCard
import com.example.sologym.ui.components.SoloTopBar
import com.example.sologym.ui.components.TimerCard
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenDrawer: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.showWorkoutPicker) {
        ModalBottomSheet(onDismissRequest = viewModel::dismissWorkoutPicker) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("ESCOLHA UM TREINO", style = MaterialTheme.typography.titleLarge)
                Text("O TREINO VOLUNTÁRIO CONCEDE 20 XP E NÃO ALTERA O STREAK.")
                uiState.availableWorkouts.forEach { workout ->
                    OutlinedButton(
                        onClick = { viewModel.selectVoluntaryWorkout(workout.treino.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            workout.treino.diaSemana.getDisplayName(
                                TextStyle.FULL,
                                Locale.getDefault()
                            ).uppercase()
                        )
                    }
                }
            }
        }
    }

    uiState.substitutePicker?.let { picker ->
        ModalBottomSheet(onDismissRequest = viewModel::dismissSubstitutes) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "SUBSTITUIR ${picker.originalExerciseName}",
                    style = MaterialTheme.typography.titleLarge
                )
                TextButton(
                    onClick = {
                        viewModel.selectOriginalExercise(picker.originalExerciseId)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "USAR EXERCÍCIO ORIGINAL",
                        modifier = Modifier.weight(1f)
                    )
                    if (picker.selectedSubstituteId == null) {
                        Icon(Icons.Default.Check, contentDescription = "Selecionado")
                    }
                }
                HorizontalDivider()
                picker.substitutes.forEach { substitute ->
                    TextButton(
                        onClick = {
                            viewModel.selectSubstitute(
                                picker.originalExerciseId,
                                substitute.id
                            )
                        },
                        enabled = !uiState.isLoadingSubstitute,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = substitute.nome,
                            modifier = Modifier.weight(1f)
                        )
                        if (picker.selectedSubstituteId == substitute.id) {
                            Icon(Icons.Default.Check, contentDescription = "Selecionado")
                        }
                    }
                }
                if (uiState.isLoadingSubstitute) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                uiState.error?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    Scaffold(
        topBar = {
            SoloTopBar(
                imageRes = com.example.sologym.R.drawable.daily,
                onMenuClick = onOpenDrawer,
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            } else if (uiState.isCompletedToday) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "MISSÃO CONCLUÍDA",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text("TREINO REALIZADO HOJE")
                    }
                }
            } else if (uiState.todayWorkout == null) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "HOJE É O DESCANSO DOS JUSTOS",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text("DESCANSAR O DIA TODO CONCEDE 15 XP.")
                        if (uiState.availableWorkouts.isEmpty()) {
                            Text("CADASTRE UM TREINO PARA TREINAR VOLUNTARIAMENTE.")
                        } else {
                            Button(
                                onClick = viewModel::showWorkoutPicker,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("ESCOLHER UM TREINO")
                            }
                        }
                    }
                }
            } else {
                if (uiState.isRestDay && !uiState.isWorkoutRunning) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("TREINO VOLUNTÁRIO • 20 XP")
                        TextButton(onClick = viewModel::showWorkoutPicker) {
                            Text("TROCAR")
                        }
                    }
                }
                if (uiState.isWorkoutRunning) {
                    TimerCard(elapsedSeconds = uiState.elapsedTime)
                }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(uiState.todayWorkout?.exercicios ?: emptyList()) { workoutExercise ->
                        val originalExercise = workoutExercise.exercicio
                        val originalExerciseId = originalExercise.exercicio.id
                        val displayedExercise = uiState.selectedSubstitutes[originalExerciseId]
                            ?: originalExercise
                        ExerciseCard(
                            exercicio = displayedExercise.exercicio,
                            series = displayedExercise.series,
                            hasSubstitutes = originalExercise.substitutos.isNotEmpty(),
                            substitutedFor = if (displayedExercise !== originalExercise) {
                                originalExercise.exercicio.nome
                            } else {
                                null
                            },
                            isCompleted = uiState.completedExercisesIds.contains(originalExerciseId),
                            onToggleCompleted = { viewModel.toggleExercise(originalExerciseId) },
                            onSubstitutesClick = {
                                viewModel.openSubstitutes(originalExerciseId)
                            }
                        )
                    }
                }

                if (uiState.isFinishing || uiState.isStarting) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (!uiState.isWorkoutRunning) {
                    Button(
                        onClick = { viewModel.startWorkout() },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                    ) {
                        Text(if (uiState.isRestDay) "INICIAR TREINO VOLUNTÁRIO" else "INICIAR MISSÃO")
                    }
                } else {
                    Button(
                        onClick = { viewModel.stopWorkout() },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("FINALIZAR MISSÃO")
                    }
                }
            }
        }
    }
}
