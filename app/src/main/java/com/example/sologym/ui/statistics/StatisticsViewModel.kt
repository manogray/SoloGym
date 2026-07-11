package com.example.sologym.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sologym.repository.HistoryRepository
import com.example.sologym.repository.StatisticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val statisticsRepository: StatisticsRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        loadStatistics()
    }

    private fun loadStatistics() {
        _uiState.update { it.copy(isLoading = true) }
        
        viewModelScope.launch {
            combine(
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
            }.catch { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}
