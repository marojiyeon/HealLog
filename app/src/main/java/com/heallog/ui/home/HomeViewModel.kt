package com.heallog.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heallog.data.local.entity.Injury
import com.heallog.data.local.entity.PainLog
import com.heallog.data.preferences.VoicePreferences
import com.heallog.data.repository.InjuryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class InjuryWithLatestLog(
    val injury: Injury,
    val latestLog: PainLog?
)

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data object Empty : HomeUiState
    data class Success(val items: List<InjuryWithLatestLog>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: InjuryRepository,
    private val voicePreferences: VoicePreferences
) : ViewModel() {

    val voiceFabEnabled: StateFlow<Boolean> = voicePreferences.voiceFabEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val uiState: StateFlow<HomeUiState> = repository.getAllActiveInjuries()
        .flatMapLatest { injuries ->
            if (injuries.isEmpty()) {
                flowOf(emptyList())
            } else {
                // Combine each injury with its latest pain log.
                // Any DB change (new log, status update) re-emits the whole list.
                combine(
                    injuries.map { injury ->
                        repository.getLatestLog(injury.id)
                            .map { log -> InjuryWithLatestLog(injury, log) }
                    }
                ) { it.toList() }
            }
        }
        .map { items ->
            if (items.isEmpty()) HomeUiState.Empty
            else HomeUiState.Success(items)
        }
        .catch { e -> emit(HomeUiState.Error(e.message ?: "오류가 발생했습니다")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState.Loading
        )
}
