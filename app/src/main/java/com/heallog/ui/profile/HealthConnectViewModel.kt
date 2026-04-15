package com.heallog.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heallog.data.repository.HealthConnectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HealthConnectUiState {
    data object NotAvailable : HealthConnectUiState
    data object PermissionDenied : HealthConnectUiState
    data object Loading : HealthConnectUiState
    data class Success(val stepsToday: Long, val sleepHours: Double?) : HealthConnectUiState
    data class Error(val message: String) : HealthConnectUiState
}

@HiltViewModel
class HealthConnectViewModel @Inject constructor(
    private val repository: HealthConnectRepository
) : ViewModel() {

    val requiredPermissions: Set<String> = repository.getPermissions()

    private val _uiState = MutableStateFlow<HealthConnectUiState>(HealthConnectUiState.Loading)
    val uiState: StateFlow<HealthConnectUiState> = _uiState.asStateFlow()

    fun checkAvailabilityAndLoad() {
        if (!repository.isAvailable()) {
            _uiState.value = HealthConnectUiState.NotAvailable
            return
        }
        viewModelScope.launch {
            _uiState.value = HealthConnectUiState.Loading
            if (!repository.hasPermissions()) {
                _uiState.value = HealthConnectUiState.PermissionDenied
                return@launch
            }
            runCatching { repository.loadHealthData() }
                .onSuccess { data ->
                    _uiState.value = HealthConnectUiState.Success(
                        stepsToday = data.stepsToday,
                        sleepHours = data.sleepHours
                    )
                }
                .onFailure { e ->
                    Log.e("HealthConnectVM", "Health data load failed", e)
                    val message = when (e) {
                        is SecurityException -> "권한이 없습니다. 설정에서 권한을 허용해 주세요."
                        is java.io.IOException -> "데이터를 읽는 중 오류가 발생했습니다."
                        else -> "Health Connect 오류가 발생했습니다."
                    }
                    _uiState.value = HealthConnectUiState.Error(message)
                }
        }
    }

    fun onPermissionsResult(granted: Set<String>) {
        if (granted.containsAll(requiredPermissions)) {
            checkAvailabilityAndLoad()
        } else {
            _uiState.value = HealthConnectUiState.PermissionDenied
        }
    }
}
