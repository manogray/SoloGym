package com.example.sologym.ui.home

import com.example.sologym.database.relation.CompleteWorkout

data class HomeUiState(
    val isLoading: Boolean = false,
    val todayWorkout: CompleteWorkout? = null,
    val isWorkoutRunning: Boolean = false,
    val elapsedTime: Long = 0,
    val completedExercisesIds: Set<Long> = emptySet(),
    val error: String? = null,
    val showFinishDialog: Boolean = false
)
