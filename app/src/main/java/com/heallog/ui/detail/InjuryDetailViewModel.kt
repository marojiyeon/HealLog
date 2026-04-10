package com.heallog.ui.detail

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heallog.data.local.entity.Injury
import com.heallog.data.local.entity.PainLog
import com.heallog.data.repository.InjuryRepository
import com.heallog.model.InjuryStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

data class NewLogFormState(
    val painLevel: Int = 5,
    val note: String = "",
    val photoUris: List<Uri> = emptyList(),
    val isExpanded: Boolean = false,
    val isSaving: Boolean = false
)

data class InjuryDetailUiState(
    val injury: Injury? = null,
    val painLogs: List<PainLog> = emptyList(),
    val newLogForm: NewLogFormState = NewLogFormState(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class InjuryDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: InjuryRepository
) : ViewModel() {

    private val injuryId: Long = checkNotNull(savedStateHandle["injuryId"])

    private val _formState = MutableStateFlow(NewLogFormState())

    val uiState: StateFlow<InjuryDetailUiState> = combine(
        repository.getInjuryById(injuryId),
        repository.getLogsForInjury(injuryId),
        _formState
    ) { injury, logs, form ->
        InjuryDetailUiState(
            injury = injury,
            painLogs = logs.sortedByDescending { it.loggedAt },
            newLogForm = form,
            isLoading = false
        )
    }
        .catch { e -> emit(InjuryDetailUiState(isLoading = false, error = e.message)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = InjuryDetailUiState()
        )

    private val _navigateBack = Channel<Unit>(Channel.BUFFERED)
    val navigateBack = _navigateBack.receiveAsFlow()

    private val _logSaved = Channel<Unit>(Channel.BUFFERED)
    val logSaved = _logSaved.receiveAsFlow()

    // Emits the bodyPartId to navigate to edit injury screen
    private val _navigateToEdit = Channel<String>(Channel.BUFFERED)
    val navigateToEdit = _navigateToEdit.receiveAsFlow()

    fun toggleLogForm() {
        _formState.update { it.copy(isExpanded = !it.isExpanded) }
    }

    fun updateLogPainLevel(value: Int) {
        _formState.update { it.copy(painLevel = value) }
    }

    fun updateLogNote(value: String) {
        _formState.update { it.copy(note = value) }
    }

    fun addLogPhoto(uri: Uri) {
        _formState.update { state ->
            if (state.photoUris.size < 3) state.copy(photoUris = state.photoUris + uri)
            else state
        }
    }

    fun removeLogPhoto(uri: Uri) {
        _formState.update { it.copy(photoUris = it.photoUris - uri) }
    }

    fun addPainLog() {
        val form = _formState.value
        viewModelScope.launch {
            _formState.update { it.copy(isSaving = true) }
            repository.insertLog(
                PainLog(
                    injuryId = injuryId,
                    painLevel = form.painLevel,
                    note = form.note.trim(),
                    photoUris = form.photoUris
                        .joinToString(",") { it.toString() }
                        .takeIf { it.isNotBlank() },
                    loggedAt = LocalDateTime.now()
                )
            )
            _formState.value = NewLogFormState(isExpanded = false)
            _logSaved.send(Unit)
        }
    }

    fun updatePainLog(log: PainLog, painLevel: Int, note: String, photoUris: List<Uri>) {
        viewModelScope.launch {
            repository.updateLog(
                log.copy(
                    painLevel = painLevel,
                    note = note.trim(),
                    photoUris = photoUris
                        .joinToString(",") { it.toString() }
                        .takeIf { it.isNotBlank() },
                    updatedAt = LocalDateTime.now()
                )
            )
        }
    }

    fun deletePainLog(log: PainLog) {
        viewModelScope.launch {
            repository.deleteLog(log)
        }
    }

    fun updateInjuryStatus(status: InjuryStatus) {
        val injury = uiState.value.injury ?: return
        viewModelScope.launch {
            repository.updateInjury(injury.copy(status = status))
        }
    }

    fun deleteInjury() {
        val injury = uiState.value.injury ?: return
        viewModelScope.launch {
            repository.deleteInjury(injury)
            _navigateBack.send(Unit)
        }
    }

    fun navigateToEditInjury() {
        val injury = uiState.value.injury ?: return
        viewModelScope.launch {
            _navigateToEdit.send(injury.bodyPart)
        }
    }
}
