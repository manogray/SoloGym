package com.example.sologym.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sologym.repository.HistoryRepository
import com.example.sologym.repository.StatisticsRepository
import com.example.sologym.repository.PlayerRepository
import com.example.sologym.repository.PlayerProfileRepository
import com.example.sologym.model.ProfileCalculations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.time.LocalDate

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val statisticsRepository: StatisticsRepository,
    private val historyRepository: HistoryRepository,
    private val playerRepository: PlayerRepository,
    private val playerProfileRepository: PlayerProfileRepository,
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

            combine(
                statistics,
                playerRepository.observePlayer(),
                playerProfileRepository.observeProfile(),
                playerProfileRepository.observeRecentMeasurements(6),
            ) { state, player, profile, measurements ->
                state.copy(
                    player = player,
                    profile = profile,
                    age = profile.dataNascimento?.let { birthDate ->
                        ProfileCalculations.ageOn(birthDate, LocalDate.now())
                    },
                    measurements = measurements.reversed(),
                )
            }.catch { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun showResetDialog() {
        _uiState.update { it.copy(showResetDialog = true) }
    }

    fun dismissResetDialog() {
        _uiState.update { it.copy(showResetDialog = false) }
    }

    fun resetPlayerProgress() {
        if (_uiState.value.isResetting) return

        _uiState.update { it.copy(showResetDialog = false, isResetting = true, error = null) }
        viewModelScope.launch {
            runCatching { playerRepository.resetProgressAndHistory() }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message) }
                }
            _uiState.update { it.copy(isResetting = false) }
        }
    }
}
