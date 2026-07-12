package com.example.sologym.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sologym.repository.HistoryRepository
import com.example.sologym.repository.StatisticsRepository
import com.example.sologym.repository.PlayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val statisticsRepository: StatisticsRepository,
    private val historyRepository: HistoryRepository,
    private val playerRepository: PlayerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            playerRepository.evaluateMissedWorkouts()
        }
        loadStatistics()
    }

    private fun loadStatistics() {
        _uiState.update { it.copy(isLoading = true) }
        
        viewModelScope.launch {
            val statistics = combine(
                statisticsRepository.getWorkoutsThisWeek(),
                statisticsRepository.getWorkoutsThisMonth(),
                statisticsRepository.getWorkoutsThisYear(),
                statisticsRepository.getTotalDuration(),
                historyRepository.getLastWorkouts(10)
            ) { week, month, year, duration, last ->
                StatisticsUiState(
                    isLoading = false,
                    workoutsThisWeek = week,
                    workoutsThisMonth = month,
                    workoutsThisYear = year,
                    totalDurationSeconds = duration ?: 0,
                    lastWorkouts = last
                )
            }

            combine(statistics, playerRepository.observePlayer()) { state, player ->
                state.copy(player = player)
            }.catch { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}
