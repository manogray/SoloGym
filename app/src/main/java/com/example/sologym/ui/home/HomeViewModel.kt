package com.example.sologym.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.os.SystemClock
import com.example.sologym.model.WorkoutTimer
import com.example.sologym.repository.HistoryRepository
import com.example.sologym.repository.WorkoutRepository
import com.example.sologym.repository.PlayerRepository
import com.example.sologym.repository.WorkoutSessionRepository
import com.example.sologym.repository.ExerciseRepository
import com.example.sologym.database.entity.ActiveWorkoutSession
import com.example.sologym.database.relation.CompleteWorkout
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val historyRepository: HistoryRepository,
    private val playerRepository: PlayerRepository,
    private val workoutSessionRepository: WorkoutSessionRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var workoutStartedAtMillis: Long? = null
    private var restoredStartedAtEpochMillis: Long? = null

    init {
        viewModelScope.launch {
            playerRepository.evaluateMissedWorkouts()
        }
        loadTodayWorkout()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadTodayWorkout() {
        val today = LocalDate.now()
        _uiState.update { it.copy(isLoading = true) }
        
        viewModelScope.launch {
            workoutSessionRepository.observeActiveSession()
                .flatMapLatest { session ->
                    if (session != null) {
                        workoutRepository.getCompleteWorkoutById(session.workoutId)
                            .map { workout -> HomeLoadResult(workout, false, session) }
                    } else {
                        workoutRepository.getCompleteWorkoutByDay(today.dayOfWeek)
                            .flatMapLatest { workout ->
                                if (workout == null) {
                                    flowOf(HomeLoadResult(null, false, null))
                                } else {
                                    historyRepository.observeWorkoutCompletedOnDate(
                                        workout.treino.id,
                                        today
                                    ).map { completed ->
                                        HomeLoadResult(workout, completed, null)
                                    }
                                }
                            }
                    }
                }
                .onEach { result ->
                    if (result.activeSession != null) {
                        restoreActiveSession(result.activeSession)
                    } else {
                        timerJob?.cancel()
                        workoutStartedAtMillis = null
                        restoredStartedAtEpochMillis = null
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            todayWorkout = result.workout,
                            isCompletedToday = result.completedToday,
                            isWorkoutRunning = result.activeSession != null,
                            isStarting = false
                        )
                    }
                }
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect()
        }
    }

    fun startWorkout() {
        val state = _uiState.value
        if (state.isCompletedToday || state.isWorkoutRunning || state.isStarting) return
        val workoutId = state.todayWorkout?.treino?.id ?: return
        val startedAtEpochMillis = System.currentTimeMillis()

        _uiState.update { it.copy(isStarting = true, elapsedTime = 0, error = null) }
        viewModelScope.launch {
            try {
                workoutSessionRepository.startSession(workoutId, startedAtEpochMillis)
            } catch (e: Exception) {
                _uiState.update { it.copy(isStarting = false, error = e.message) }
            }
        }
    }

    private fun restoreActiveSession(session: ActiveWorkoutSession) {
        if (restoredStartedAtEpochMillis == session.startedAtEpochMillis) return

        workoutStartedAtMillis = WorkoutTimer.restoreMonotonicStart(
            startedAtEpochMillis = session.startedAtEpochMillis,
            currentEpochMillis = System.currentTimeMillis(),
            currentElapsedRealtimeMillis = SystemClock.elapsedRealtime()
        )
        restoredStartedAtEpochMillis = session.startedAtEpochMillis
        updateElapsedTime()
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                updateElapsedTime()
                delay(1000)
            }
        }
    }

    private fun updateElapsedTime(): Long {
        val startedAt = workoutStartedAtMillis ?: return _uiState.value.elapsedTime
        val elapsedSeconds = WorkoutTimer.elapsedSeconds(
            startedAtMillis = startedAt,
            currentTimeMillis = SystemClock.elapsedRealtime()
        )
        _uiState.update { it.copy(elapsedTime = elapsedSeconds) }
        return elapsedSeconds
    }

    fun stopWorkout() {
        val currentState = _uiState.value
        if (!currentState.isWorkoutRunning || currentState.isFinishing || currentState.isCompletedToday) {
            return
        }

        val finalElapsedTime = updateElapsedTime()
        val stateBeforeFinish = _uiState.value
        val workoutId = stateBeforeFinish.todayWorkout?.treino?.id ?: return
        timerJob?.cancel()
        _uiState.update { it.copy(isWorkoutRunning = false, isFinishing = true) }
        
        viewModelScope.launch {
            try {
                historyRepository.completeWorkout(
                    workoutId = workoutId,
                    completedAt = LocalDateTime.now(),
                    durationSeconds = finalElapsedTime
                )
                workoutStartedAtMillis = null
                restoredStartedAtEpochMillis = null
                _uiState.update {
                    it.copy(
                        elapsedTime = 0,
                        completedExercisesIds = emptySet(),
                        selectedSubstitutes = emptyMap(),
                        substitutePicker = null,
                        showFinishDialog = false,
                        isFinishing = false,
                        isCompletedToday = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isFinishing = false,
                        isWorkoutRunning = true,
                        error = e.message
                    )
                }
                startTimer()
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

    fun openSubstitutes(originalExerciseId: Long) {
        val original = _uiState.value.todayWorkout?.exercicios
            ?.firstOrNull { it.exercicio.exercicio.id == originalExerciseId }
            ?.exercicio
            ?: return

        if (original.substitutos.isEmpty()) return

        _uiState.update { state ->
            state.copy(
                substitutePicker = SubstitutePickerUiState(
                    originalExerciseId = originalExerciseId,
                    originalExerciseName = original.exercicio.nome,
                    substitutes = original.substitutos.sortedBy { it.nome },
                    selectedSubstituteId = state.selectedSubstitutes[originalExerciseId]
                        ?.exercicio
                        ?.id
                ),
                error = null
            )
        }
    }

    fun dismissSubstitutes() {
        if (_uiState.value.isLoadingSubstitute) return
        _uiState.update { it.copy(substitutePicker = null) }
    }

    fun selectOriginalExercise(originalExerciseId: Long) {
        _uiState.update { state ->
            state.copy(
                selectedSubstitutes = state.selectedSubstitutes - originalExerciseId,
                substitutePicker = null
            )
        }
    }

    fun selectSubstitute(originalExerciseId: Long, substituteId: Long) {
        if (_uiState.value.isLoadingSubstitute) return
        _uiState.update { it.copy(isLoadingSubstitute = true, error = null) }

        viewModelScope.launch {
            try {
                val substitute = exerciseRepository.getCompleteExercise(substituteId)
                    ?: throw IllegalStateException("Exercício substituto não encontrado")
                _uiState.update { state ->
                    state.copy(
                        selectedSubstitutes = state.selectedSubstitutes +
                            (originalExerciseId to substitute),
                        substitutePicker = null,
                        isLoadingSubstitute = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoadingSubstitute = false, error = e.message)
                }
            }
        }
    }
}

private data class HomeLoadResult(
    val workout: CompleteWorkout?,
    val completedToday: Boolean,
    val activeSession: ActiveWorkoutSession?
)
