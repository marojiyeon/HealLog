package com.heallog.ui.detail.hospital

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heallog.data.local.entity.Medication
import com.heallog.data.repository.HospitalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

data class AddMedicationUiState(
    val name: String = "",
    val nameError: Boolean = false,
    val dosage: String = "",
    val dosageError: Boolean = false,
    val frequency: String = "1일 3회",
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate? = null,
    val sideEffectNote: String = "",
    val isActive: Boolean = true,
    val isSaving: Boolean = false,
    val isEditMode: Boolean = false
)

@HiltViewModel
class AddMedicationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: HospitalRepository
) : ViewModel() {

    private val injuryId: Long = checkNotNull(savedStateHandle["injuryId"])
    private val medicationId: Long = checkNotNull(savedStateHandle["medicationId"])

    private val NO_MEDICATION_ID = -1L

    private val _uiState = MutableStateFlow(AddMedicationUiState())
    val uiState: StateFlow<AddMedicationUiState> = _uiState

    private val _navigateBack = Channel<Unit>(Channel.BUFFERED)
    val navigateBack = _navigateBack.receiveAsFlow()

    val frequencyOptions = listOf("1일 1회", "1일 2회", "1일 3회", "필요시", "기타")

    init {
        val isEditMode = medicationId != NO_MEDICATION_ID
        _uiState.update { it.copy(isEditMode = isEditMode) }

        if (isEditMode) {
            viewModelScope.launch {
                val med = repository.getMedicationsForInjury(injuryId).let { flow ->
                    var result: Medication? = null
                    flow.collect { meds ->
                        result = meds.find { it.id == medicationId }
                    }
                    result
                }
                med?.let {
                    _uiState.update { state ->
                        state.copy(
                            name = it.name,
                            dosage = it.dosage,
                            frequency = it.frequency,
                            startDate = it.startDate,
                            endDate = it.endDate,
                            sideEffectNote = it.sideEffectNote ?: "",
                            isActive = it.isActive
                        )
                    }
                }
            }
        }
    }

    fun updateName(value: String) {
        _uiState.update { it.copy(name = value, nameError = false) }
    }

    fun updateDosage(value: String) {
        _uiState.update { it.copy(dosage = value, dosageError = false) }
    }

    fun updateFrequency(value: String) {
        _uiState.update { it.copy(frequency = value) }
    }

    fun updateStartDate(value: LocalDate) {
        _uiState.update { it.copy(startDate = value) }
    }

    fun updateEndDate(value: LocalDate?) {
        _uiState.update { it.copy(endDate = value) }
    }

    fun updateSideEffectNote(value: String) {
        _uiState.update { it.copy(sideEffectNote = value) }
    }

    fun toggleIsActive() {
        _uiState.update { it.copy(isActive = !it.isActive) }
    }

    fun saveMedication() {
        val state = _uiState.value
        var hasError = false

        if (state.name.isBlank()) {
            _uiState.update { it.copy(nameError = true) }
            hasError = true
        }

        if (state.dosage.isBlank()) {
            _uiState.update { it.copy(dosageError = true) }
            hasError = true
        }

        if (hasError) return

        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            val medication = Medication(
                id = if (state.isEditMode) medicationId else 0,
                injuryId = injuryId,
                hospitalVisitId = null,
                name = state.name,
                dosage = state.dosage,
                frequency = state.frequency,
                startDate = state.startDate,
                endDate = state.endDate,
                sideEffectNote = state.sideEffectNote.takeIf { it.isNotBlank() },
                isActive = state.isActive,
                updatedAt = if (state.isEditMode) LocalDateTime.now() else null
            )

            if (state.isEditMode) {
                repository.updateMedication(medication)
            } else {
                repository.insertMedication(medication)
            }

            _navigateBack.send(Unit)
        }
    }
}
