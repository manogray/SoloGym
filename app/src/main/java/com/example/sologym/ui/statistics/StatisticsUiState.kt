package com.example.sologym.ui.statistics

import com.example.sologym.database.entity.HistoricoTreino

data class StatisticsUiState(
    val isLoading: Boolean = false,
    val workoutsThisWeek: Int = 0,
    val workoutsThisMonth: Int = 0,
    val workoutsThisYear: Int = 0,
    val totalDurationSeconds: Long = 0,
    val lastWorkouts: List<HistoricoTreino> = emptyList(),
    val error: String? = null
)
