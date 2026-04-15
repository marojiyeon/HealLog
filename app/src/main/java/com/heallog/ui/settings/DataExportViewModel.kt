package com.heallog.ui.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heallog.util.DataExportManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ExportResult {
    data class Success(val uri: Uri, val mimeType: String) : ExportResult
    data class Error(val message: String) : ExportResult
}

@HiltViewModel
class DataExportViewModel @Inject constructor(
    private val exportManager: DataExportManager
) : ViewModel() {

    private val _isExporting = MutableStateFlow(false)
    val isExporting: StateFlow<Boolean> = _isExporting.asStateFlow()

    private val _exportResult = MutableStateFlow<ExportResult?>(null)
    val exportResult: StateFlow<ExportResult?> = _exportResult.asStateFlow()

    fun exportAsCsv() {
        if (_isExporting.value) return
        viewModelScope.launch {
            _isExporting.value = true
            _exportResult.value = null
            runCatching { exportManager.exportToCsv() }
                .onSuccess { uri -> _exportResult.value = ExportResult.Success(uri, "application/zip") }
                .onFailure { e -> _exportResult.value = ExportResult.Error(e.message ?: "알 수 없는 오류") }
            _isExporting.value = false
        }
    }

    fun exportAsJson() {
        if (_isExporting.value) return
        viewModelScope.launch {
            _isExporting.value = true
            _exportResult.value = null
            runCatching { exportManager.exportToJson() }
                .onSuccess { uri -> _exportResult.value = ExportResult.Success(uri, "application/json") }
                .onFailure { e -> _exportResult.value = ExportResult.Error(e.message ?: "알 수 없는 오류") }
            _isExporting.value = false
        }
    }

    fun clearResult() {
        _exportResult.value = null
    }
}
