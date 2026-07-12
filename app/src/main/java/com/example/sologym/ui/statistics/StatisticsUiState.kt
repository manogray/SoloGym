package com.example.sologym.ui.statistics

import com.example.sologym.database.entity.HistoricoTreino
import com.example.sologym.database.entity.Player

data class StatisticsUiState(
    val isLoading: Boolean = false,
    val workoutsThisWeek: Int = 0,
    val workoutsThisMonth: Int = 0,
    val workoutsThisYear: Int = 0,
    val totalDurationSeconds: Long = 0,
    val lastWorkouts: List<HistoricoTreino> = emptyList(),
    val player: Player = Player(),
    val error: String? = null
)
