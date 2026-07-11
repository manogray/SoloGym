package com.example.sologym.ui.workouts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sologym.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutUiState())
    val uiState: StateFlow<WorkoutUiState> = _uiState.asStateFlow()

    fun onDeleteClick(treino: com.example.sologym.database.entity.Treino) {
        _uiState.update { it.copy(workoutToDelete = treino) }
    }

    fun onDismissDeleteDialog() {
        _uiState.update { it.copy(workoutToDelete = null) }
    }

    fun deleteWorkout(treino: com.example.sologym.database.entity.Treino) {
        viewModelScope.launch {
            workoutRepository.deleteWorkout(treino)
            _uiState.update { it.copy(workoutToDelete = null) }
        }
    }

    init {
        loadWorkouts()
    }

    private fun loadWorkouts() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            workoutRepository.getAllWorkouts()
                .onEach { list ->
                    _uiState.update { it.copy(isLoading = false, workouts = list) }
                }
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect()
        }
    }
}
