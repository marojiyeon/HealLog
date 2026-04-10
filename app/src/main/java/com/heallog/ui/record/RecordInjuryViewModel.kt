package com.heallog.ui.record

import android.net.Uri
import android.util.Log
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

internal const val MAX_PHOTOS = 3
private const val NO_INJURY_ID = -1L

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
    val isSaving: Boolean = false,
    val isEditMode: Boolean = false,
    val photoError: String? = null,
    val saveError: String? = null
)

@HiltViewModel
class RecordInjuryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: InjuryRepository
) : ViewModel() {

    private val bodyPartId: String = checkNotNull(savedStateHandle["bodyPartId"])
    private val injuryId: Long = savedStateHandle.get<Long>("injuryId") ?: NO_INJURY_ID

    private val _uiState = MutableStateFlow(
        RecordInjuryUiState(
            bodyPartId = bodyPartId,
            bodyPartNameKo = BodyParts.findById(bodyPartId)?.nameKo ?: bodyPartId
        )
    )
    val uiState: StateFlow<RecordInjuryUiState> = _uiState.asStateFlow()

    // One-shot navigation event
    private val _navigateBack = Channel<Unit>(Channel.BUFFERED)
    val navigateBack = _navigateBack.receiveAsFlow()

    // The existing injury loaded in edit mode, used when saving updates
    private var existingInjury: Injury? = null

    // Captured after loadExistingInjury() to detect unsaved changes in edit mode
    private var _originalState: RecordInjuryUiState? = null

    init {
        if (injuryId != NO_INJURY_ID) {
            loadExistingInjury()
        }
    }

    private fun loadExistingInjury() {
        viewModelScope.launch {
            try {
                val injury = repository.getInjuryById(injuryId).first()
                existingInjury = injury
                if (injury != null) {
                    _uiState.update {
                        it.copy(
                            title = injury.title,
                            description = injury.description,
                            painLevel = injury.painLevel,
                            isPainLevelTouched = true,
                            occurredDate = injury.occurredAt,
                            isEditMode = true
                        )
                    }
                    _originalState = _uiState.value
                }
            } catch (e: Exception) {
                Log.e("RecordInjuryViewModel", "Failed to load injury $injuryId", e)
                _navigateBack.send(Unit)
            }
        }
    }

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
        if (current.size >= MAX_PHOTOS) {
            _uiState.update { it.copy(photoError = "사진은 최대 ${MAX_PHOTOS}장까지 추가할 수 있습니다.") }
            return
        }
        _uiState.update { it.copy(photoUris = current + uri) }
    }

    fun clearPhotoError() {
        _uiState.update { it.copy(photoError = null) }
    }

    fun hasUnsavedChanges(): Boolean {
        val current = _uiState.value
        val original = _originalState
        return if (current.isEditMode && original != null) {
            current.title != original.title ||
            current.description != original.description ||
            current.painLevel != original.painLevel ||
            current.photoUris != original.photoUris
        } else {
            current.title.isNotBlank() ||
            current.description.isNotBlank() ||
            current.isPainLevelTouched ||
            current.photoUris.isNotEmpty()
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
            try {
                if (state.isEditMode) {
                    val existing = existingInjury ?: run {
                        _uiState.update { it.copy(isSaving = false) }
                        return@launch
                    }
                    repository.updateInjury(
                        existing.copy(
                            title = state.title.trim(),
                            description = state.description.trim(),
                            painLevel = state.painLevel,
                            occurredAt = state.occurredDate,
                            updatedAt = LocalDateTime.now()
                        )
                    )
                } else {
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
                }
                _navigateBack.send(Unit)
            } catch (e: Exception) {
                Log.e("RecordInjuryViewModel", "Failed to save injury", e)
                _uiState.update { it.copy(isSaving = false, saveError = "저장 실패. 다시 시도해주세요.") }
            }
        }
    }

    fun clearSaveError() {
        _uiState.update { it.copy(saveError = null) }
    }
}
