package com.example.sologym.ui.exercises

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sologym.database.entity.Exercicio
import com.example.sologym.database.entity.Serie
import com.example.sologym.repository.ExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseFormViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val exerciseId: Long? = savedStateHandle.get<String>("id")?.toLongOrNull()

    private val _uiState = MutableStateFlow(ExerciseFormUiState(id = exerciseId))
    val uiState: StateFlow<ExerciseFormUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            // Load available exercises for substitution (excluding current one if editing)
            exerciseRepository.getAllExercises()
                .onEach { list ->
                    _uiState.update { it.copy(availableSubstitutes = list.filter { e -> e.id != exerciseId }) }
                }.launchIn(viewModelScope)

            if (exerciseId != null) {
                _uiState.update { it.copy(isLoading = true) }
                val completeExercise = exerciseRepository.getCompleteExercise(exerciseId)
                if (completeExercise != null) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            nome = completeExercise.exercicio.nome,
                            descansoSegundos = completeExercise.exercicio.descansoSegundos.toString(),
                            series = completeExercise.series.map { s -> 
                                SerieUiState(id = s.id, ordem = s.ordem, repeticoes = s.repeticoes.toString()) 
                            },
                            selectedSubstitutesIds = completeExercise.substitutos.map { sub -> sub.id }.toSet()
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Exercício não encontrado") }
                }
            }
        }
    }

    fun onNomeChange(nome: String) {
        _uiState.update { it.copy(nome = nome) }
    }

    fun onDescansoChange(descanso: String) {
        if (descanso.isEmpty() || descanso.all { it.isDigit() }) {
            _uiState.update { it.copy(descansoSegundos = descanso) }
        }
    }

    fun onSerieRepsChange(index: Int, reps: String) {
        if (reps.isEmpty() || reps.all { it.isDigit() }) {
            _uiState.update { state ->
                val newSeries = state.series.toMutableList()
                newSeries[index] = newSeries[index].copy(repeticoes = reps)
                state.copy(series = newSeries)
            }
        }
    }

    fun addSerie() {
        _uiState.update { state ->
            val nextOrder = (state.series.maxOfOrNull { it.ordem } ?: 0) + 1
            state.copy(series = state.series + SerieUiState(ordem = nextOrder))
        }
    }

    fun removeSerie(index: Int) {
        _uiState.update { state ->
            if (state.series.size > 1) {
                val newSeries = state.series.toMutableList()
                newSeries.removeAt(index)
                // Reorder
                val reordered = newSeries.mapIndexed { i, s -> s.copy(ordem = i + 1) }
                state.copy(series = reordered)
            } else state
        }
    }

    fun toggleSubstitute(subId: Long) {
        _uiState.update { state ->
            val newSelected = if (state.selectedSubstitutesIds.contains(subId)) {
                state.selectedSubstitutesIds - subId
            } else {
                state.selectedSubstitutesIds + subId
            }
            state.copy(selectedSubstitutesIds = newSelected)
        }
    }

    fun save() {
        val state = _uiState.value
        if (state.nome.isBlank()) {
            _uiState.update { it.copy(error = "Nome é obrigatório") }
            return
        }
        val descanso = state.descansoSegundos.toIntOrNull() ?: 0
        if (descanso <= 0) {
            _uiState.update { it.copy(error = "Descanso inválido") }
            return
        }
        if (state.series.any { it.repeticoes.toIntOrNull() ?: 0 <= 0 }) {
            _uiState.update { it.copy(error = "Todas as séries devem ter repetições") }
            return
        }

        viewModelScope.launch {
            try {
                val exercicio = Exercicio(
                    id = exerciseId ?: 0,
                    nome = state.nome,
                    descansoSegundos = descanso
                )
                val series = state.series.map { s ->
                    Serie(
                        id = s.id,
                        exercicioId = exerciseId ?: 0,
                        ordem = s.ordem,
                        repeticoes = s.repeticoes.toInt()
                    )
                }
                
                if (exerciseId == null) {
                    exerciseRepository.insertExercise(exercicio, series, state.selectedSubstitutesIds.toList())
                } else {
                    exerciseRepository.updateExercise(exercicio, series, state.selectedSubstitutesIds.toList())
                }
                _uiState.update { it.copy(isSaved = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}
