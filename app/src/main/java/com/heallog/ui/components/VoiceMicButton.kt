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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.heallog.model.VoiceCommand
import com.heallog.ui.theme.HealLogTheme
import com.heallog.util.SpeechState
import com.heallog.util.VoiceCommandParser
import com.heallog.util.rememberSpeechRecognizerManager

@Composable
fun VoiceMicButton(
    onResult: (String) -> Unit,
    onCommand: (VoiceCommand) -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    val context = LocalContext.current
    val manager = rememberSpeechRecognizerManager()
    val state by manager.state.collectAsState()

    var showPermissionDeniedDialog by remember { mutableStateOf(false) }
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (granted) {
            manager.startListening()
        } else {
            showPermissionDeniedDialog = true
        }
    }

    // Handle result state
    LaunchedEffect(state) {
        if (state is SpeechState.Result) {
            val text = (state as SpeechState.Result).text
            if (text.isNotBlank()) {
                val command = VoiceCommandParser.parse(text)
                if (command is VoiceCommand.TextInput) {
                    onResult(command.text)
                } else {
                    onResult(text)
                    onCommand(command)
                }
            }
            manager.reset()
        }
    }

    if (showPermissionDeniedDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDeniedDialog = false },
            title = { Text("마이크 권한 필요") },
            text = { Text("음성 입력을 사용하려면 마이크 권한이 필요합니다.\n설정에서 마이크 권한을 허용해 주세요.") },
            confirmButton = {
                TextButton(onClick = { showPermissionDeniedDialog = false }) {
                    Text("확인")
                }
            }
        )
    }

    // Don't show the button if speech recognition is not available
    if (!manager.isAvailable) return

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            when (state) {
                is SpeechState.Listening -> {
                    PulsingMicIcon(
                        onClick = { manager.stopListening() }
                    )
                }
                is SpeechState.Processing -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
                else -> {
                    IconButton(onClick = {
                        if (hasPermission) {
                            manager.startListening()
                        } else {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "음성 입력",
                            tint = tint
                        )
                    }
                }
            }
        }

        // Partial result preview
        val partialText = (state as? SpeechState.Listening)?.partialText.orEmpty()
        if (partialText.isNotBlank()) {
            Spacer(Modifier.height(4.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.widthIn(max = 200.dp)
            ) {
                Text(
                    text = partialText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // Error message
        val error = (state as? SpeechState.Error)?.message.orEmpty()
        if (error.isNotBlank()) {
            Spacer(Modifier.height(2.dp))
            Text(
                text = error,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun PulsingMicIcon(onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = "음성 인식 중 (탭하여 중지)",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.scale(scale)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun VoiceMicButtonPreview() {
    HealLogTheme {
        VoiceMicButton(onResult = {}, onCommand = {})
    }
}
