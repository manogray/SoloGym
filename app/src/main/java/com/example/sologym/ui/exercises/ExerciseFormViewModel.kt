package com.example.sologym.ui.exercises

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sologym.database.entity.Exercicio
import com.example.sologym.database.entity.Serie
import com.example.sologym.repository.ExerciseRepository
import com.example.sologym.model.AerobicExercise
import com.example.sologym.model.ExerciseType
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
                            tipo = completeExercise.exercicio.tipo,
                            nome = completeExercise.exercicio.nome,
                            aerobicExercise = AerobicExercise.entries.firstOrNull {
                                it.displayName == completeExercise.exercicio.nome
                            },
                            duracaoMinutos = completeExercise.exercicio.duracaoMinutos
                                ?.toString().orEmpty(),
                            descansoSegundos = completeExercise.exercicio.descansoSegundos.toString(),
                            series = completeExercise.series.map { s -> 
                                SerieUiState(
                                    id = s.id,
                                    ordem = s.ordem,
                                    repeticoes = s.repeticoes.toString(),
                                    carga = s.carga.toEditableString()
                                )
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

    fun onTipoChange(tipo: ExerciseType) {
        if (exerciseId != null) return
        _uiState.update { state ->
            if (state.tipo == tipo) {
                state
            } else {
                state.copy(
                    tipo = tipo,
                    nome = "",
                    aerobicExercise = null,
                    duracaoMinutos = "",
                    descansoSegundos = "",
                    series = listOf(SerieUiState(ordem = 1)),
                    selectedSubstitutesIds = emptySet(),
                    error = null,
                )
            }
        }
    }

    fun onAerobicExerciseChange(exercise: AerobicExercise) {
        _uiState.update {
            it.copy(aerobicExercise = exercise, nome = exercise.displayName, error = null)
        }
    }

    fun onDurationChange(duration: String) {
        if (duration.isEmpty() || duration.all(Char::isDigit)) {
            _uiState.update { it.copy(duracaoMinutos = duration, error = null) }
        }
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

    fun onSerieCargaChange(index: Int, carga: String) {
        if (carga.isValidDecimalInput()) {
            _uiState.update { state ->
                val newSeries = state.series.toMutableList()
                newSeries[index] = newSeries[index].copy(carga = carga)
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
        if (state.tipo == null) {
            _uiState.update { it.copy(error = "Selecione o tipo do exercício") }
            return
        }
        if (state.nome.isBlank()) {
            _uiState.update {
                it.copy(
                    error = if (state.tipo == ExerciseType.AEROBIC) {
                        "Selecione o exercício aeróbico"
                    } else {
                        "Nome é obrigatório"
                    }
                )
            }
            return
        }
        if (state.tipo == ExerciseType.AEROBIC) {
            val duration = state.duracaoMinutos.toIntOrNull() ?: 0
            if (duration <= 0) {
                _uiState.update { it.copy(error = "Informe uma duração válida") }
                return
            }
            saveExercise(state, descanso = 0, duration = duration, series = emptyList())
            return
        }

        val descanso = state.descansoSegundos.toIntOrNull() ?: 0
        if (descanso <= 0) {
            _uiState.update { it.copy(error = "Descanso inválido") }
            return
        }
        if (state.series.any { (it.repeticoes.toIntOrNull() ?: 0) <= 0 }) {
            _uiState.update { it.copy(error = "Todas as séries devem ter repetições") }
            return
        }

        if (state.series.any { it.carga.toCargaOrNull() == null }) {
            _uiState.update { it.copy(error = "Informe uma carga válida em todas as séries") }
            return
        }

        val series = state.series.map { s ->
            Serie(
                id = s.id,
                exercicioId = exerciseId ?: 0,
                ordem = s.ordem,
                repeticoes = s.repeticoes.toInt(),
                carga = s.carga.toCargaOrNull() ?: 0.0
            )
        }
        saveExercise(state, descanso, duration = null, series)
    }

    private fun saveExercise(
        state: ExerciseFormUiState,
        descanso: Int,
        duration: Int?,
        series: List<Serie>,
    ) {
        viewModelScope.launch {
            try {
                val exercicio = Exercicio(
                    id = exerciseId ?: 0,
                    nome = state.nome,
                    descansoSegundos = descanso,
                    tipo = requireNotNull(state.tipo),
                    duracaoMinutos = duration,
                )
                val substitutes = if (state.tipo == ExerciseType.STRENGTH) {
                    state.selectedSubstitutesIds.toList()
                } else {
                    emptyList()
                }
                
                if (exerciseId == null) {
                    exerciseRepository.insertExercise(exercicio, series, substitutes)
                } else {
                    exerciseRepository.updateExercise(exercicio, series, substitutes)
                }
                _uiState.update { it.copy(isSaved = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    private fun String.isValidDecimalInput(): Boolean {
        if (isEmpty()) return true
        if (count { it == '.' || it == ',' } > 1) return false
        return all { it.isDigit() || it == '.' || it == ',' }
    }

    private fun String.toCargaOrNull(): Double? {
        if (isBlank()) return 0.0
        return replace(',', '.').toDoubleOrNull()?.takeIf { it >= 0.0 }
    }

    private fun Double.toEditableString(): String =
        if (this % 1.0 == 0.0) toInt().toString() else toString()
}
