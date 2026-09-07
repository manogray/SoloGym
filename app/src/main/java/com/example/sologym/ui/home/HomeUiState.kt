package com.example.sologym.ui.home

import com.example.sologym.database.relation.CompleteWorkout
import com.example.sologym.database.relation.CompleteExercise
import com.example.sologym.database.entity.Exercicio

data class HomeUiState(
    val isLoading: Boolean = false,
    val todayWorkout: CompleteWorkout? = null,
    val availableWorkouts: List<CompleteWorkout> = emptyList(),
    val isRestDay: Boolean = false,
    val showWorkoutPicker: Boolean = false,
    val isCompletedToday: Boolean = false,
    val isWorkoutRunning: Boolean = false,
    val isStarting: Boolean = false,
    val isFinishing: Boolean = false,
    val elapsedTime: Long = 0,
    val completedExercisesIds: Set<Long> = emptySet(),
    val selectedSubstitutes: Map<Long, CompleteExercise> = emptyMap(),
    val substitutePicker: SubstitutePickerUiState? = null,
    val isLoadingSubstitute: Boolean = false,
    val error: String? = null,
    val showFinishDialog: Boolean = false
)

data class SubstitutePickerUiState(
    val originalExerciseId: Long,
    val originalExerciseName: String,
    val substitutes: List<Exercicio>,
    val selectedSubstituteId: Long?
)
