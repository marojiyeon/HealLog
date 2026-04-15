package com.heallog.ui.settings

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataExportScreen(
    onBack: () -> Unit,
    viewModel: DataExportViewModel = hiltViewModel()
) {
    val isExporting by viewModel.isExporting.collectAsState()
    val exportResult by viewModel.exportResult.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(exportResult) {
        val result = exportResult ?: return@LaunchedEffect
        when (result) {
            is ExportResult.Error -> {
                snackbarHostState.showSnackbar("내보내기 실패: ${result.message}")
                viewModel.clearResult()
            }
            is ExportResult.Success -> Unit // handled by share button
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("데이터 내보내기") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "모든 부상 기록을 파일로 내보냅니다.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            if (isExporting) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "내보내는 중...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                val successResult = exportResult as? ExportResult.Success
                if (successResult != null) {
                    Button(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = successResult.mimeType
                                putExtra(Intent.EXTRA_STREAM, successResult.uri)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "파일 공유"))
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("공유하기")
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { viewModel.clearResult() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("다시 내보내기")
                    }
                } else {
                    Button(
                        onClick = { viewModel.exportAsCsv() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("CSV로 내보내기")
                    }
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { viewModel.exportAsJson() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("JSON으로 내보내기")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                text = "CSV: 부상, 통증 일지, 병원 방문, 약 기록이 ZIP으로 묶여 저장됩니다.\nJSON: 모든 데이터가 계층 구조로 저장됩니다.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DataExportScreenPreview() {
    DataExportScreen(onBack = {})
}
