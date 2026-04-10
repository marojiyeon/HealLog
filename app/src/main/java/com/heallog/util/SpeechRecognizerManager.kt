package com.heallog.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface SpeechState {
    data object Idle : SpeechState
    data class Listening(val partialText: String = "") : SpeechState
    data object Processing : SpeechState
    data class Result(val text: String) : SpeechState
    data class Error(val message: String) : SpeechState
}

class SpeechRecognizerManager(private val context: Context) {

    val isAvailable: Boolean = SpeechRecognizer.isRecognitionAvailable(context)

    private val _state = MutableStateFlow<SpeechState>(SpeechState.Idle)
    val state: StateFlow<SpeechState> = _state.asStateFlow()

    private var continuousMode = false

    private val recognizer: SpeechRecognizer? =
        if (isAvailable) SpeechRecognizer.createSpeechRecognizer(context) else null

    private val listener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            _state.value = SpeechState.Listening()
        }

        override fun onBeginningOfSpeech() {
            // keep Listening state
        }

        override fun onRmsChanged(rmsdB: Float) {}

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onEndOfSpeech() {
            _state.value = SpeechState.Processing
        }

        override fun onError(error: Int) {
            val message = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "오디오 오류가 발생했습니다"
                SpeechRecognizer.ERROR_CLIENT -> "클라이언트 오류가 발생했습니다"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "마이크 권한이 필요합니다"
                SpeechRecognizer.ERROR_NETWORK -> "네트워크 연결을 확인해주세요"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "네트워크 연결 시간이 초과되었습니다"
                SpeechRecognizer.ERROR_NO_MATCH -> "음성을 인식하지 못했습니다"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "음성 인식기가 사용 중입니다"
                SpeechRecognizer.ERROR_SERVER -> "서버 오류가 발생했습니다"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "음성 입력 시간이 초과되었습니다"
                else -> "알 수 없는 오류가 발생했습니다"
            }
            _state.value = SpeechState.Error(message)
        }

        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val text = matches?.firstOrNull().orEmpty()
            _state.value = SpeechState.Result(text)
            if (continuousMode && text.isNotBlank()) {
                startListeningInternal()
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {
            val partial = partialResults
                ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                ?.firstOrNull()
                .orEmpty()
            if (partial.isNotBlank()) {
                _state.value = SpeechState.Listening(partial)
            }
        }

        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    init {
        recognizer?.setRecognitionListener(listener)
    }

    fun startListening(languageCode: String = "ko-KR", continuous: Boolean = false) {
        continuousMode = continuous
        startListeningInternal(languageCode)
    }

    private fun startListeningInternal(languageCode: String = "ko-KR") {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageCode)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 5000L)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 3000L)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        recognizer?.startListening(intent)
    }

    fun stopListening() {
        continuousMode = false
        recognizer?.stopListening()
        _state.value = SpeechState.Idle
    }

    fun reset() {
        _state.value = SpeechState.Idle
    }

    fun destroy() {
        continuousMode = false
        recognizer?.destroy()
    }
}

@Composable
fun rememberSpeechRecognizerManager(
    context: Context = LocalContext.current
): SpeechRecognizerManager {
    val manager = remember(context) { SpeechRecognizerManager(context) }
    DisposableEffect(manager) {
        onDispose { manager.destroy() }
    }
    return manager
}
