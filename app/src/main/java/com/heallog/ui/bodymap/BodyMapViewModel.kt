package com.heallog.ui.bodymap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heallog.data.repository.InjuryRepository
import com.heallog.model.BodyPart
import com.heallog.model.InjuryStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BodyMapUiState(
    val activeInjuryPartIds: Set<String> = emptySet(),
    val selectedPart: BodyPart? = null,
    val isFrontView: Boolean = true
)

@HiltViewModel
class BodyMapViewModel @Inject constructor(
    private val repository: InjuryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BodyMapUiState())
    val uiState: StateFlow<BodyMapUiState> = _uiState.asStateFlow()

    init {
        observeActiveInjuries()
    }

    private fun observeActiveInjuries() {
        viewModelScope.launch {
            repository.getAllActiveInjuries().collect { injuries ->
                val activePartIds = injuries
                    .filter { it.status != InjuryStatus.HEALED }
                    .map { it.bodyPart }
                    .toSet()
                _uiState.update { it.copy(activeInjuryPartIds = activePartIds) }
            }
        }
    }

    fun onBodyPartSelected(bodyPart: BodyPart) {
        _uiState.update { state ->
            val newSelection = if (state.selectedPart?.id == bodyPart.id) null else bodyPart
            state.copy(selectedPart = newSelection)
        }
    }

    fun onViewToggled(isFront: Boolean) {
        _uiState.update { it.copy(isFrontView = isFront, selectedPart = null) }
    }
}
