package com.example.sologym.ui.workouts

import com.example.sologym.database.entity.Treino

data class WorkoutUiState(
    val isLoading: Boolean = false,
    val workouts: List<Treino> = emptyList(),
    val workoutToDelete: Treino? = null,
    val error: String? = null
)
