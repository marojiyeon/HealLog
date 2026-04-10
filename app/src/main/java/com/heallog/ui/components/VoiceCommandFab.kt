package com.heallog.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.heallog.R
import androidx.core.content.ContextCompat
import com.heallog.model.VoiceCommand
import com.heallog.util.SpeechState
import com.heallog.util.VoiceCommandParser
import com.heallog.util.rememberSpeechRecognizerManager

/**
 * A floating action button that listens to voice commands.
 *
 * Supports voice commands to:
 * - Start recording an injury
 * - Navigate to body map
 * - Other navigation commands
 *
 * Handles microphone permissions and speech recognition state management.
 */
@Composable
fun VoiceCommandFab(
    onNavigateToBodyMap: () -> Unit,
    onShowMessage: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val manager = rememberSpeechRecognizerManager()
    val state by manager.state.collectAsState()
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED
        )
    }
    var showAudioRationale by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (granted && manager.isAvailable) manager.startListening()
    }

    // Handle voice command results
    LaunchedEffect(state) {
        if (state is SpeechState.Result) {
            val text = (state as SpeechState.Result).text
            val command = VoiceCommandParser.parse(text)
            handleVoiceCommand(command, onNavigateToBodyMap, onShowMessage)
            manager.reset()
        }
    }

    if (showAudioRationale) {
        AlertDialog(
            onDismissRequest = { showAudioRationale = false },
            title = { Text(stringResource(R.string.mic_permission_title)) },
            text = { Text(stringResource(R.string.mic_permission_rationale)) },
            confirmButton = {
                TextButton(onClick = {
                    showAudioRationale = false
                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }) { Text(stringResource(R.string.action_allow)) }
            },
            dismissButton = {
                TextButton(onClick = { showAudioRationale = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    if (!manager.isAvailable) return

    val isListening = state is SpeechState.Listening
    val isProcessing = state is SpeechState.Processing

    val infiniteTransition = rememberInfiniteTransition(label = "voice_fab_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "voice_fab_scale"
    )

    SmallFloatingActionButton(
        onClick = {
            when {
                isListening || isProcessing -> manager.stopListening()
                hasPermission -> manager.startListening()
                else -> showAudioRationale = true
            }
        },
        containerColor = when {
            isListening -> MaterialTheme.colorScheme.error
            isProcessing -> MaterialTheme.colorScheme.secondaryContainer
            else -> MaterialTheme.colorScheme.secondaryContainer
        },
        modifier = modifier.then(if (isListening) Modifier.scale(pulseScale) else Modifier)
    ) {
        if (isProcessing) {
            CircularProgressIndicator(
                modifier = Modifier,
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        } else {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = stringResource(if (isListening) R.string.cd_voice_listening else R.string.cd_voice_command),
                tint = if (isListening) MaterialTheme.colorScheme.onError
                else MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

/**
 * Handles a parsed voice command by executing the appropriate action.
 */
private fun handleVoiceCommand(
    command: VoiceCommand,
    onNavigateToBodyMap: () -> Unit,
    onShowMessage: (String) -> Unit
) {
    when (command) {
        is VoiceCommand.StartRecord -> onNavigateToBodyMap()
        is VoiceCommand.NavigateTo -> {
            if (command.destination == "bodymap") onNavigateToBodyMap()
        }
        is VoiceCommand.GoHome -> {
            onShowMessage("이미 홈 화면입니다")
        }
        else -> {
            val feedbackText = when (command) {
                is VoiceCommand.TextInput -> "인식됨: ${command.text}"
                is VoiceCommand.AddNote -> "메모: ${command.text}"
                is VoiceCommand.SetPainLevel -> "통증: ${command.level}/10"
                else -> "음성 입력: $command"
            }
            onShowMessage(feedbackText)
        }
    }
}
