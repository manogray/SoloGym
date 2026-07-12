package com.example.sologym.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sologym.database.entity.HistoricoTreino
import com.example.sologym.model.WorkoutStatus
import com.example.sologym.repository.HistoryRepository
import com.example.sologym.repository.WorkoutRepository
import com.example.sologym.repository.PlayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val historyRepository: HistoryRepository,
    private val playerRepository: PlayerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            playerRepository.evaluateMissedWorkouts()
        }
        loadTodayWorkout()
    }

    private fun loadTodayWorkout() {
        val today = LocalDate.now().dayOfWeek
        _uiState.update { it.copy(isLoading = true) }
        
        viewModelScope.launch {
            workoutRepository.getCompleteWorkoutByDay(today)
                .onEach { workout ->
                    _uiState.update { it.copy(isLoading = false, todayWorkout = workout) }
                }
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect()
        }
    }

    fun startWorkout() {
        _uiState.update { it.copy(isWorkoutRunning = true) }
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _uiState.update { it.copy(elapsedTime = it.elapsedTime + 1) }
            }
        }
    }

    fun stopWorkout() {
        timerJob?.cancel()
        _uiState.update { it.copy(isWorkoutRunning = false) }
        
        val currentState = _uiState.value
        val workoutId = currentState.todayWorkout?.treino?.id
        
        viewModelScope.launch {
            historyRepository.insertHistory(
                HistoricoTreino(
                    treinoId = workoutId,
                    data = LocalDateTime.now(),
                    duracaoSegundos = currentState.elapsedTime
                )
            )
            playerRepository.recordCompletedWorkout()
            // Reset session state
            _uiState.update { 
                it.copy(
                    elapsedTime = 0,
                    completedExercisesIds = emptySet(),
                    showFinishDialog = false
                )
            }
        }
    }

    fun toggleExercise(exerciseId: Long) {
        _uiState.update { state ->
            val newCompleted = if (state.completedExercisesIds.contains(exerciseId)) {
                state.completedExercisesIds - exerciseId
            } else {
                state.completedExercisesIds + exerciseId
            }
            state.copy(completedExercisesIds = newCompleted)
        }
    }
}
