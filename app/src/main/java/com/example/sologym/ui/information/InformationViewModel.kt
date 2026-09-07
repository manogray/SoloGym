package com.example.sologym.ui.information

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sologym.database.entity.PlayerProfile
import com.example.sologym.database.entity.BodyMeasurement
import com.example.sologym.repository.PlayerProfileRepository
import com.example.sologym.model.ProfileImages
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class InformationViewModel @Inject constructor(
    private val profileRepository: PlayerProfileRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(InformationUiState())
    val uiState: StateFlow<InformationUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                profileRepository.observeProfile(),
                profileRepository.observeLatestMeasurement(),
            ) { profile, latestMeasurement -> profile to latestMeasurement }
                .collect { (profile, latestMeasurement) ->
                    val current = _uiState.value
                    _uiState.value = current.copy(
                        isLoading = false,
                        nome = if (current.isLoading) profile.nome else current.nome,
                        dataNascimento = if (current.isLoading) {
                            profile.dataNascimento
                        } else {
                            current.dataNascimento
                        },
                        fotoUri = if (current.isLoading) {
                            ProfileImages.normalizedName(profile.fotoUri)
                        } else {
                            current.fotoUri
                        },
                        currentWeightKg = latestMeasurement?.pesoKg,
                        currentHeightCm = latestMeasurement?.alturaCm,
                    )
                }
        }
    }

    fun onNameChange(value: String) = change { copy(nome = value.take(100)) }
    fun onBirthDateChange(value: LocalDate) = change { copy(dataNascimento = value) }
    fun onWeightChange(value: String) = change { copy(pesoKg = decimalInput(value)) }
    fun onHeightChange(value: String) = change { copy(alturaCm = decimalInput(value)) }
    fun onPhotoChange(value: String) = change { copy(fotoUri = value) }

    fun save() {
        val state = _uiState.value
        if (state.isSaving) return

        val weight = state.pesoKg.replace(',', '.').toDoubleOrNull()
        val height = state.alturaCm.replace(',', '.').toDoubleOrNull()
        val hasExistingMeasurement = state.currentWeightKg != null && state.currentHeightCm != null
        val isAddingMeasurement = state.pesoKg.isNotBlank() || state.alturaCm.isNotBlank()
        val error = when {
            state.nome.isBlank() -> "Informe o nome."
            state.dataNascimento == null -> "Informe a data de nascimento."
            state.dataNascimento.isAfter(LocalDate.now()) -> "A data de nascimento não pode estar no futuro."
            !hasExistingMeasurement && (weight == null || weight <= 0) -> "Informe um peso válido."
            !hasExistingMeasurement && (height == null || height <= 0) -> "Informe uma altura válida."
            isAddingMeasurement && (weight == null || weight <= 0) -> "Informe um novo peso válido."
            isAddingMeasurement && (height == null || height <= 0) -> "Informe uma nova altura válida."
            else -> null
        }
        if (error != null) {
            _uiState.update { it.copy(error = error, saved = false) }
            return
        }

        _uiState.update { it.copy(isSaving = true, error = null, saved = false) }
        viewModelScope.launch {
            runCatching {
                val measurement = if (!hasExistingMeasurement || isAddingMeasurement) {
                    BodyMeasurement(
                        recordedAt = LocalDateTime.now(),
                        pesoKg = requireNotNull(weight),
                        alturaCm = requireNotNull(height),
                    )
                } else {
                    null
                }
                profileRepository.save(
                    PlayerProfile(
                        nome = state.nome.trim(),
                        dataNascimento = state.dataNascimento,
                        fotoUri = ProfileImages.normalizedName(state.fotoUri),
                    ),
                    measurement,
                )
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        saved = true,
                        pesoKg = "",
                        alturaCm = "",
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(isSaving = false, error = throwable.message ?: "Não foi possível salvar.")
                }
            }
        }
    }

    private fun change(transform: InformationUiState.() -> InformationUiState) {
        _uiState.update { it.transform().copy(saved = false, error = null) }
    }

    private fun decimalInput(value: String): String = value
        .filter { it.isDigit() || it == ',' || it == '.' }
        .take(7)
}
