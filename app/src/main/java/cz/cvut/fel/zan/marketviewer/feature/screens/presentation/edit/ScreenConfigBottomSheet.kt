package cz.cvut.fel.zan.marketviewer.feature.screens.presentation.edit

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cz.cvut.fel.zan.marketviewer.core.presentation.theme.MarketViewerTheme
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.MarketViewerScreen
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenConfigBottomSheet(
    deviceId: Int,
    screenToEdit: MarketViewerScreen? = null,
    onDismiss: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel : ScreenConfigViewModel = koinViewModel()
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)


    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = Modifier.fillMaxHeight()
    ) {

        Button(onClick = {}) {
            Text("Close")
        }
    }
}

@Preview
@Composable
fun ScreenConfigBottomSheetPreview() {
    MarketViewerTheme {
        ScreenConfigBottomSheet(
            deviceId = 1,
            onDismiss = {},
            onSaveSuccess = {}
        )
    }
}