package com.heallog.ui.detail.hospital

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heallog.data.local.entity.HospitalVisit
import com.heallog.data.local.entity.Medication
import com.heallog.data.repository.HospitalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class HospitalTabViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: HospitalRepository
) : ViewModel() {

    private val injuryId: Long = checkNotNull(savedStateHandle["injuryId"])

    val visits: StateFlow<List<HospitalVisit>> = repository
        .getVisitsForInjury(injuryId)
        .map { it.sortedByDescending { v -> v.visitDate } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val medications: StateFlow<List<Medication>> = repository
        .getMedicationsForInjury(injuryId)
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _visitDeleted = Channel<Unit>(Channel.BUFFERED)
    val visitDeleted = _visitDeleted.receiveAsFlow()

    fun deleteVisit(visit: HospitalVisit) {
        viewModelScope.launch {
            repository.deleteVisit(visit)
            _visitDeleted.send(Unit)
        }
    }

    fun deleteMedication(med: Medication) {
        viewModelScope.launch { repository.deleteMedication(med) }
    }

    fun toggleMedicationActive(med: Medication) {
        viewModelScope.launch {
            repository.updateMedication(med.copy(isActive = !med.isActive, updatedAt = LocalDateTime.now()))
        }
    }
}
