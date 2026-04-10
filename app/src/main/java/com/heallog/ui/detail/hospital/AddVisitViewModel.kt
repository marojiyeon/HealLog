package com.heallog.ui.detail.hospital

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heallog.data.local.entity.HospitalVisit
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

data class AddVisitUiState(
    val hospitalName: String = "",
    val hospitalNameError: Boolean = false,
    val doctorName: String = "",
    val visitDate: LocalDate = LocalDate.now(),
    val diagnosis: String = "",
    val treatmentNote: String = "",
    val treatmentNoteError: Boolean = false,
    val nextAppointment: LocalDate? = null,
    val cost: String = "",
    val isInsuranceCovered: Boolean? = null,
    val photoUris: List<String> = emptyList(),
    val isSaving: Boolean = false,
    val isEditMode: Boolean = false
)

@HiltViewModel
class AddVisitViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: HospitalRepository
) : ViewModel() {

    private val injuryId: Long = checkNotNull(savedStateHandle["injuryId"])
    private val visitId: Long = checkNotNull(savedStateHandle["visitId"])

    private val NO_VISIT_ID = -1L

    private val _uiState = MutableStateFlow(AddVisitUiState())
    val uiState: StateFlow<AddVisitUiState> = _uiState

    private val _navigateBack = Channel<Unit>(Channel.BUFFERED)
    val navigateBack = _navigateBack.receiveAsFlow()

    init {
        val isEditMode = visitId != NO_VISIT_ID
        _uiState.update { it.copy(isEditMode = isEditMode) }

        if (isEditMode) {
            viewModelScope.launch {
                val visit = repository.getVisitById(visitId).let { flow ->
                    var result: HospitalVisit? = null
                    flow.collect { result = it }
                    result
                }
                visit?.let {
                    _uiState.update { state ->
                        state.copy(
                            hospitalName = it.hospitalName,
                            doctorName = it.doctorName ?: "",
                            visitDate = it.visitDate,
                            diagnosis = it.diagnosis ?: "",
                            treatmentNote = it.treatmentNote,
                            nextAppointment = it.nextAppointment,
                            cost = it.cost?.toString() ?: "",
                            isInsuranceCovered = it.isInsuranceCovered,
                            photoUris = it.photoUris?.split(",") ?: emptyList()
                        )
                    }
                }
            }
        }
    }

    fun updateHospitalName(value: String) {
        _uiState.update { it.copy(hospitalName = value, hospitalNameError = false) }
    }

    fun updateDoctorName(value: String) {
        _uiState.update { it.copy(doctorName = value) }
    }

    fun updateVisitDate(value: LocalDate) {
        _uiState.update { it.copy(visitDate = value) }
    }

    fun updateDiagnosis(value: String) {
        _uiState.update { it.copy(diagnosis = value) }
    }

    fun updateTreatmentNote(value: String) {
        _uiState.update { it.copy(treatmentNote = value, treatmentNoteError = false) }
    }

    fun updateNextAppointment(value: LocalDate?) {
        _uiState.update { it.copy(nextAppointment = value) }
    }

    fun updateCost(value: String) {
        _uiState.update { it.copy(cost = value) }
    }

    fun updateIsInsuranceCovered(value: Boolean?) {
        _uiState.update { it.copy(isInsuranceCovered = value) }
    }

    fun saveVisit() {
        val state = _uiState.value
        var hasError = false

        if (state.hospitalName.isBlank()) {
            _uiState.update { it.copy(hospitalNameError = true) }
            hasError = true
        }

        if (state.treatmentNote.isBlank()) {
            _uiState.update { it.copy(treatmentNoteError = true) }
            hasError = true
        }

        if (hasError) return

        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            val photoUrisString = if (state.photoUris.isNotEmpty()) {
                state.photoUris.joinToString(",")
            } else {
                null
            }

            val visit = HospitalVisit(
                id = if (state.isEditMode) visitId else 0,
                injuryId = injuryId,
                visitDate = state.visitDate,
                hospitalName = state.hospitalName,
                doctorName = state.doctorName.takeIf { it.isNotBlank() },
                diagnosis = state.diagnosis.takeIf { it.isNotBlank() },
                treatmentNote = state.treatmentNote,
                nextAppointment = state.nextAppointment,
                cost = state.cost.toIntOrNull(),
                isInsuranceCovered = state.isInsuranceCovered,
                photoUris = photoUrisString,
                createdAt = LocalDateTime.now(),
                updatedAt = if (state.isEditMode) LocalDateTime.now() else null
            )

            if (state.isEditMode) {
                repository.updateVisit(visit)
            } else {
                repository.insertVisit(visit)
            }

            _navigateBack.send(Unit)
        }
    }
}
