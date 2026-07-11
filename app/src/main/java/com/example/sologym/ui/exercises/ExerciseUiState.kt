package com.example.sologym.ui.exercises

import com.example.sologym.database.entity.Exercicio

data class ExerciseUiState(
    val isLoading: Boolean = false,
    val exerciseList: List<Exercicio> = emptyList(),
    val searchText: String = "",
    val showDeleteDialog: Exercicio? = null,
    val error: String? = null
)
