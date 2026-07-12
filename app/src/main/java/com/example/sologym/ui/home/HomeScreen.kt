package com.example.sologym.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NewReleases
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            SoloTopBar(
                "MISSÃO DIÁRIA",
                Icons.Outlined.NewReleases,
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
            } else if (uiState.todayWorkout == null) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "HOJE É O DESCANSO DOS JUSTOS",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            } else {
                if (uiState.isWorkoutRunning) {
                    TimerCard(elapsedSeconds = uiState.elapsedTime)
                }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(uiState.todayWorkout?.exercicios ?: emptyList()) { workoutExercise ->
                        val exercise = workoutExercise.exercicio.exercicio
                        ExerciseCard(
                            exercicio = exercise,
                            series = workoutExercise.exercicio.series,
                            isCompleted = uiState.completedExercisesIds.contains(exercise.id),
                            onToggleCompleted = { viewModel.toggleExercise(exercise.id) },
                            onSubstitutesClick = { /* Show substitutes */ }
                        )
                    }
                }

                if (!uiState.isWorkoutRunning) {
                    Button(
                        onClick = { viewModel.startWorkout() },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                    ) {
                        Text("INICIAR MISSÃO")
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
