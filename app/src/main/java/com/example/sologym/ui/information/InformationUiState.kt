package com.example.sologym.ui.information

import java.time.LocalDate
import com.example.sologym.model.ProfileImages

data class InformationUiState(
    val isLoading: Boolean = true,
    val nome: String = "",
    val dataNascimento: LocalDate? = null,
    val pesoKg: String = "",
    val alturaCm: String = "",
    val currentWeightKg: Double? = null,
    val currentHeightCm: Double? = null,
    val fotoUri: String = ProfileImages.DEFAULT,
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null,
)
