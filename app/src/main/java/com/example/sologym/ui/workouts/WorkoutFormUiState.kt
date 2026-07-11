package com.example.sologym.ui.workouts

import com.example.sologym.database.entity.Exercicio
import java.time.DayOfWeek

data class WorkoutFormUiState(
    val id: Long? = null,
    val diaSemana: DayOfWeek? = null,
    val selectedExercises: List<Exercicio> = emptyList(),
    val availableExercises: List<Exercicio> = emptyList(),
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
    val showExerciseSelection: Boolean = false
)
