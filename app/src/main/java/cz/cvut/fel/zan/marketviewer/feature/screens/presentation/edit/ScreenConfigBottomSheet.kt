package cz.cvut.fel.zan.marketviewer.feature.screens.presentation.edit

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.cvut.fel.zan.marketviewer.R
import cz.cvut.fel.zan.marketviewer.core.presentation.theme.MarketViewerTheme
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.ClockScreen
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.CryptoScreen
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.CryptoTimeFrame
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.GraphType
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.MarketViewerScreen
import cz.cvut.fel.zan.marketviewer.feature.screens.presentation.edit.form.CryptoConfigContent
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ScreenConfigBottomSheet(
    deviceId: Int,
    screenToEdit: MarketViewerScreen,
    onDismiss: () -> Unit,
    onSaveSuccess: (MarketViewerScreen) -> Unit,
    viewModel : ScreenConfigViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is ScreenConfigViewModel.ScreenConfigEffect.SaveSuccess -> {
                    onSaveSuccess(effect.updatedScreen)
                    onDismiss()
                }
            }
        }
    }

    ScreenConfigBottomSheetContent(
        screenToEdit = screenToEdit,
        onDismiss = onDismiss,
        isLoading = state.isLoading,
        saveErrorMsg = state.saveErrorMsg,
        onSave = { viewModel.onEvent(ScreenConfigViewModel.ScreenConfigEvents.SaveScreenConfig(deviceId, it)) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenConfigBottomSheetContent(
    screenToEdit: MarketViewerScreen,
    onDismiss: () -> Unit,
    onSave: (MarketViewerScreen) -> Unit,
    isLoading: Boolean,
    saveErrorMsg: String?
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)


    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .padding(bottom = 32.dp)
        ) {

            // title row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_edit_24),
                    contentDescription = "edit icon"
                )
                Text(
                    text = "Edit Screen",
                    style = MaterialTheme.typography.headlineSmall,
                )
            }

            when (screenToEdit) {
                is CryptoScreen -> {
                    CryptoConfigContent(
                        screen = screenToEdit,
                        onSave = {
                             onSave(it)
                        }
                    )
                }
                is ClockScreen -> {
//                    ClockConfigContent(
//                        screen = screenToEdit,
//                        onSave = { /* ... */ }
//                    )
                }
                else -> {
                    Text("Unsupported screen type")
                }
            }

            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            } else if (saveErrorMsg != null) {
                Text(saveErrorMsg)
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ScreenConfigBottomSheetPreview() {
    val screenToEdit = CryptoScreen(id = 1, position = 1, assetName = "bro",  timeFrame = CryptoTimeFrame.DAY, currency = "", graphType = GraphType.LINE, displayGraph = false, simpleDisplay = false)

    MarketViewerTheme {
        ScreenConfigBottomSheetContent (
            onDismiss = {},
            screenToEdit = screenToEdit,
            isLoading = false,
            saveErrorMsg = null,
            onSave = {}
        )
    }
}