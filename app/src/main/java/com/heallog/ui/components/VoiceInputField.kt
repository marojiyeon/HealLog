package com.heallog.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.heallog.model.VoiceCommand
import com.heallog.ui.theme.HealLogTheme

/**
 * Drop-in replacement for OutlinedTextField with an integrated mic button.
 * When a voice result arrives, the text is appended to the current value.
 */
@Composable
fun VoiceInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    singleLine: Boolean = false,
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
    onCommand: (VoiceCommand) -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label?.let { { Text(it) } },
        placeholder = placeholder?.let { { Text(it) } },
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        isError = isError,
        supportingText = supportingText,
        shape = RoundedCornerShape(12.dp),
        trailingIcon = {
            VoiceMicButton(
                onResult = { recognized ->
                    // Append with a space if existing text is non-empty
                    val appended = if (value.isNotEmpty()) "$value $recognized" else recognized
                    onValueChange(appended)
                },
                onCommand = onCommand
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun VoiceInputFieldPreview() {
    HealLogTheme {
        VoiceInputField(
            value = "왼쪽 무릎 인대",
            onValueChange = {},
            label = "부상 제목",
            placeholder = "부상 제목을 입력하세요"
        )
    }
}
