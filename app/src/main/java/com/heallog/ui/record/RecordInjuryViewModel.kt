package com.heallog.ui.record

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heallog.data.local.entity.Injury
import com.heallog.data.repository.InjuryRepository
import com.heallog.model.BodyParts
import com.heallog.model.InjuryStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

internal const val MAX_PHOTOS = 3

data class RecordInjuryUiState(
    val bodyPartId: String = "",
    val bodyPartNameKo: String = "",
    val title: String = "",
    val titleError: Boolean = false,
    val description: String = "",
    val painLevel: Int = 0,
    val isPainLevelTouched: Boolean = false,
    val painLevelError: Boolean = false,
    val occurredDate: LocalDate = LocalDate.now(),
    val photoUris: List<Uri> = emptyList(),
    val isSaving: Boolean = false
)

@HiltViewModel
class RecordInjuryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: InjuryRepository
) : ViewModel() {

    private val bodyPartId: String = checkNotNull(savedStateHandle["bodyPartId"])

    private val _uiState = MutableStateFlow(
        RecordInjuryUiState(
            bodyPartId = bodyPartId,
            bodyPartNameKo = BodyParts.findById(bodyPartId)?.nameKo ?: bodyPartId
        )
    )
    val uiState: StateFlow<RecordInjuryUiState> = _uiState.asStateFlow()

    // One-shot navigation event: screen collects this and calls onNavigateBack()
    private val _navigateBack = Channel<Unit>(Channel.BUFFERED)
    val navigateBack = _navigateBack.receiveAsFlow()

    fun updateTitle(value: String) {
        _uiState.update { it.copy(title = value, titleError = false) }
    }

    fun updateDescription(value: String) {
        _uiState.update { it.copy(description = value) }
    }

    fun updatePainLevel(value: Int) {
        _uiState.update {
            it.copy(painLevel = value, isPainLevelTouched = true, painLevelError = false)
        }
    }

    fun updateDate(date: LocalDate) {
        _uiState.update { it.copy(occurredDate = date) }
    }

    fun addPhoto(uri: Uri) {
        val current = _uiState.value.photoUris
        if (current.size < MAX_PHOTOS) {
            _uiState.update { it.copy(photoUris = current + uri) }
        }
    }

    fun removePhoto(uri: Uri) {
        _uiState.update { it.copy(photoUris = it.photoUris - uri) }
    }

    fun saveInjury() {
        val state = _uiState.value
        val titleBlank = state.title.isBlank()
        val painNotSet = !state.isPainLevelTouched

        if (titleBlank || painNotSet) {
            _uiState.update {
                it.copy(titleError = titleBlank, painLevelError = painNotSet)
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            repository.insertInjury(
                Injury(
                    bodyPart = state.bodyPartId,
                    title = state.title.trim(),
                    description = state.description.trim(),
                    painLevel = state.painLevel,
                    occurredAt = state.occurredDate,
                    createdAt = LocalDateTime.now(),
                    status = InjuryStatus.ACTIVE
                )
            )
            _navigateBack.send(Unit)
        }
    }
}
