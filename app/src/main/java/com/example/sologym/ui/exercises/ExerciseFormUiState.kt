package com.example.sologym.ui.exercises

import com.example.sologym.database.entity.Exercicio
import com.example.sologym.database.entity.Serie
import com.example.sologym.model.AerobicExercise
import com.example.sologym.model.ExerciseType

data class ExerciseFormUiState(
    val id: Long? = null,
    val tipo: ExerciseType? = null,
    val nome: String = "",
    val aerobicExercise: AerobicExercise? = null,
    val duracaoMinutos: String = "",
    val descansoSegundos: String = "",
    val series: List<SerieUiState> = listOf(SerieUiState(ordem = 1)),
    val availableSubstitutes: List<Exercicio> = emptyList(),
    val selectedSubstitutesIds: Set<Long> = emptySet(),
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

data class SerieUiState(
    val id: Long = 0,
    val ordem: Int,
    val repeticoes: String = "",
    val carga: String = "0"
)
