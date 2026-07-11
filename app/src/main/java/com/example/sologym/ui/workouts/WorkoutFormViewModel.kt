package com.example.sologym.ui.workouts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sologym.database.entity.Treino
import com.example.sologym.database.entity.TreinoExercicio
import com.example.sologym.repository.ExerciseRepository
import com.example.sologym.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import javax.inject.Inject

@HiltViewModel
class WorkoutFormViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val workoutId: Long? = savedStateHandle.get<String>("id")?.toLongOrNull()

    private val _uiState = MutableStateFlow(WorkoutFormUiState(id = workoutId))
    val uiState: StateFlow<WorkoutFormUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            // Load available exercises
            exerciseRepository.getAllExercises()
                .onEach { list ->
                    _uiState.update { it.copy(availableExercises = list) }
                }.launchIn(viewModelScope)

            if (workoutId != null) {
                _uiState.update { it.copy(isLoading = true) }
                workoutRepository.getCompleteWorkoutById(workoutId)
                    .filterNotNull()
                    .take(1)
                    .onEach { completeWorkout ->
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                diaSemana = completeWorkout.treino.diaSemana,
                                selectedExercises = completeWorkout.exercicios
                                    .sortedBy { we -> we.treinoExercicio.ordem }
                                    .map { we -> we.exercicio.exercicio }
                            )
                        }
                    }.catch { e ->
                        _uiState.update { it.copy(isLoading = false, error = e.message) }
                    }.collect()
            }
        }
    }

    fun onDaySelected(day: DayOfWeek) {
        _uiState.update { it.copy(diaSemana = day) }
    }

    fun toggleExerciseSelection() {
        _uiState.update { it.copy(showExerciseSelection = !it.showExerciseSelection) }
    }

    fun addExercise(exercise: com.example.sologym.database.entity.Exercicio) {
        _uiState.update { it.copy(selectedExercises = it.selectedExercises + exercise) }
    }

    fun removeExercise(index: Int) {
        _uiState.update { 
            val newList = it.selectedExercises.toMutableList()
            newList.removeAt(index)
            it.copy(selectedExercises = newList)
        }
    }

    fun moveExerciseUp(index: Int) {
        if (index > 0) {
            _uiState.update { 
                val newList = it.selectedExercises.toMutableList()
                val item = newList.removeAt(index)
                newList.add(index - 1, item)
                it.copy(selectedExercises = newList)
            }
        }
    }

    fun moveExerciseDown(index: Int) {
        if (index < _uiState.value.selectedExercises.size - 1) {
            _uiState.update { 
                val newList = it.selectedExercises.toMutableList()
                val item = newList.removeAt(index)
                newList.add(index + 1, item)
                it.copy(selectedExercises = newList)
            }
        }
    }

    fun save() {
        val state = _uiState.value
        if (state.diaSemana == null) {
            _uiState.update { it.copy(error = "Selecione um dia da semana") }
            return
        }
        if (state.selectedExercises.isEmpty()) {
            _uiState.update { it.copy(error = "Adicione pelo menos um exercício") }
            return
        }

        viewModelScope.launch {
            try {
                // Check if another workout exists for this day (RN-08)
                // If editing, exclude current workoutId
                
                val treino = Treino(
                    id = workoutId ?: 0,
                    diaSemana = state.diaSemana
                )
                
                val exercises = state.selectedExercises.mapIndexed { index, exercise ->
                    TreinoExercicio(
                        treinoId = workoutId ?: 0,
                        exercicioId = exercise.id,
                        ordem = index + 1
                    )
                }

                if (workoutId == null) {
                    workoutRepository.insertWorkout(treino, exercises)
                } else {
                    workoutRepository.updateWorkout(treino, exercises)
                }
                _uiState.update { it.copy(isSaved = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}
