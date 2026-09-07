package com.example.sologym.ui.statistics

import com.example.sologym.database.entity.HistoricoTreino
import com.example.sologym.database.entity.Player
import com.example.sologym.database.entity.PlayerProfile
import com.example.sologym.database.entity.BodyMeasurement

data class StatisticsUiState(
    val isLoading: Boolean = false,
    val workoutsThisWeek: Int = 0,
    val workoutsThisMonth: Int = 0,
    val workoutsThisYear: Int = 0,
    val totalDurationSeconds: Long = 0,
    val lastWorkouts: List<HistoricoTreino> = emptyList(),
    val player: Player = Player(),
    val profile: PlayerProfile = PlayerProfile(),
    val age: Int? = null,
    val measurements: List<BodyMeasurement> = emptyList(),
    val showResetDialog: Boolean = false,
    val isResetting: Boolean = false,
    val error: String? = null
)
