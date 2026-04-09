package com.heallog.ui.bodymap

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.heallog.model.BodyPart
import com.heallog.ui.theme.HealLogTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BodyMapScreen(
    onNavigateToRecord: (bodyPartId: String) -> Unit,
    viewModel: BodyMapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("부위 선택") })
        }
    ) { innerPadding ->
        BodyMapContent(
            uiState = uiState,
            onBodyPartSelected = viewModel::onBodyPartSelected,
            onViewToggled = viewModel::onViewToggled,
            onRecordClicked = { partId -> onNavigateToRecord(partId) },
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun BodyMapContent(
    uiState: BodyMapUiState,
    onBodyPartSelected: (BodyPart) -> Unit,
    onViewToggled: (isFront: Boolean) -> Unit,
    onRecordClicked: (bodyPartId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(8.dp))

        // --- Selected body part label ---
        SelectedPartLabel(selectedPart = uiState.selectedPart)

        Spacer(Modifier.height(12.dp))

        // --- Front / Back toggle ---
        ViewToggle(
            isFrontView = uiState.isFrontView,
            onToggle = onViewToggled
        )

        Spacer(Modifier.height(8.dp))

        // --- Body map canvas ---
        BodyMapView(
            activeInjuryParts = uiState.activeInjuryPartIds,
            selectedPartId = uiState.selectedPart?.id,
            isFrontView = uiState.isFrontView,
            onBodyPartSelected = onBodyPartSelected,
            modifier = Modifier.weight(1f)
        )

        Spacer(Modifier.height(16.dp))

        // --- Record Injury button ---
        AnimatedVisibility(
            visible = uiState.selectedPart != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            val part = uiState.selectedPart
            Button(
                onClick = { part?.let { onRecordClicked(it.id) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = if (part != null) "${part.nameKo} 부상 기록" else "부상 기록",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun SelectedPartLabel(selectedPart: BodyPart?) {
    val text = selectedPart?.let { "${it.nameKo} (${it.nameEn})" }
        ?: "부위를 탭하여 선택하세요"

    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = if (selectedPart != null)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ViewToggle(
    isFrontView: Boolean,
    onToggle: (isFront: Boolean) -> Unit
) {
    SingleChoiceSegmentedButtonRow {
        SegmentedButton(
            selected = isFrontView,
            onClick = { onToggle(true) },
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
            icon = {}
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("앞면")
            }
        }
        SegmentedButton(
            selected = !isFrontView,
            onClick = { onToggle(false) },
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
            icon = {}
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("뒷면")
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun BodyMapContentPreview_NoneSelected() {
    HealLogTheme {
        BodyMapContent(
            uiState = BodyMapUiState(
                activeInjuryPartIds = setOf("left_knee", "right_shoulder"),
                selectedPart = null,
                isFrontView = true
            ),
            onBodyPartSelected = {},
            onViewToggled = {},
            onRecordClicked = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun BodyMapContentPreview_Selected() {
    HealLogTheme {
        BodyMapContent(
            uiState = BodyMapUiState(
                activeInjuryPartIds = setOf("left_knee"),
                selectedPart = com.heallog.model.BodyParts.LEFT_KNEE,
                isFrontView = true
            ),
            onBodyPartSelected = {},
            onViewToggled = {},
            onRecordClicked = {}
        )
    }
}
