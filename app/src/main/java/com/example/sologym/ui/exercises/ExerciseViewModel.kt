package com.example.sologym.ui.exercises

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sologym.database.entity.Exercicio
import com.example.sologym.repository.ExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExerciseUiState())
    val uiState: StateFlow<ExerciseUiState> = _uiState.asStateFlow()

    init {
        loadExercises()
    }

    private fun loadExercises() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            exerciseRepository.getAllExercises()
                .onEach { list ->
                    _uiState.update { it.copy(isLoading = false, exerciseList = list) }
                }
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect()
        }
    }

    fun onDeleteClick(exercicio: Exercicio) {
        _uiState.update { it.copy(showDeleteDialog = exercicio) }
    }

    fun dismissDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = null) }
    }

    fun deleteExercise(exercicio: Exercicio) {
        viewModelScope.launch {
            try {
                exerciseRepository.deleteExercise(exercicio)
                _uiState.update { it.copy(showDeleteDialog = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}
