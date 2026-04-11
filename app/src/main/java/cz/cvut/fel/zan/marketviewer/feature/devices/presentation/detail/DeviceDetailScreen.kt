package cz.cvut.fel.zan.marketviewer.feature.devices.presentation.detail

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.cvut.fel.zan.marketviewer.R
import cz.cvut.fel.zan.marketviewer.core.presentation.theme.MarketViewerTheme
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.ClockScreen
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.CryptoScreen
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.MarketViewerScreen
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun DeviceDetailScreen(
    onBackClicked: () -> Unit,
    onDeviceDeleted: (Int) -> Unit,
    viewModel: DeviceDetailViewModel = koinViewModel()
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is DeviceDetailViewModel.DeviceDetailEffect.GoBackToDeviceList -> {
                    onBackClicked()
                }
                is DeviceDetailViewModel.DeviceDetailEffect.GoBackWithDeleteResult -> {
                    onDeviceDeleted(effect.deviceId)
                }

            }
        }
    }


    DeviceDetailScreenContent(
        deviceId = state.deviceId,
        isLoading = state.isLoading,
        deviceName = state.deviceName,
        deviceHash = state.deviceHash,
        errorMsg = state.errorMsg,
        nameChangeErrorMsg = state.nameChangeErrorMsg,
        isEditingName = state.isEditingName,
        screens = state.screens,
        onEditNameToggle = { viewModel.onEvent(DeviceDetailViewModel.DeviceDetailEvents.ToggleNameEdit) },
        onDeleteClick = { viewModel.onEvent(DeviceDetailViewModel.DeviceDetailEvents.DeleteDeviceClick) },
        onBackClicked = onBackClicked,
        onDeleteScreenClick = { viewModel.onEvent(DeviceDetailViewModel.DeviceDetailEvents.DeleteScreenLocally(it)) },
        onUndoDeleteScreen = { screen, index -> viewModel.onEvent(DeviceDetailViewModel.DeviceDetailEvents.UndoDeleteScreen(screen, index)) },
        onConfirmDeleteScreen = { screen, index -> viewModel.onEvent(DeviceDetailViewModel.DeviceDetailEvents.ConfirmDeleteScreen(screen, index)) },
        onScreenItemMove = { fromIndex, toIndex -> viewModel.onEvent(DeviceDetailViewModel.DeviceDetailEvents.ReorderScreenLocally(fromIndex, toIndex)) },
        onScreenReorderConfirm = { viewModel.onEvent(DeviceDetailViewModel.DeviceDetailEvents.ConfirmReorderScreens) },
        onDeviceNameChange = { viewModel.onEvent(DeviceDetailViewModel.DeviceDetailEvents.ChangeDeviceName(it)) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceDetailScreenContent(
    deviceId: Int,
    isLoading: Boolean,
    deviceName: String?,
    deviceHash: String?,
    errorMsg: String?,
    nameChangeErrorMsg: String?,
    isEditingName: Boolean,
    screens: List<MarketViewerScreen>?,
    onEditNameToggle: () -> Unit,
    onDeleteClick: () -> Unit,
    onBackClicked: () -> Unit,
    onDeleteScreenClick: (MarketViewerScreen) -> Unit,
    onUndoDeleteScreen: (MarketViewerScreen, Int) -> Unit,
    onConfirmDeleteScreen: (MarketViewerScreen, Int) -> Unit,
    onScreenItemMove: (fromIndex: Int, toIndex: Int) -> Unit,
    onScreenReorderConfirm: () -> Unit,
    onDeviceNameChange: (String) -> Unit
) {
    var showDeleteDeviceDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Device detail")
                },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_arrow_back_24),
                            contentDescription = "Go back"
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = { showDeleteDeviceDialog = true },
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_delete_forever_24),
                            contentDescription = "Delete icon",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(end = 5.dp)
                        )
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                },
                windowInsets = WindowInsets(0.dp),
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            }
            else if (errorMsg != null) {
                Text(text = errorMsg, color = MaterialTheme.colorScheme.error)
            }
            else {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    //device name
                    DeviceNameTitle(
                        deviceName = deviceName,
                        isEditing = isEditingName,
                        nameChangeErrorMsg = nameChangeErrorMsg,
                        onNameChangeSave = onDeviceNameChange,
                        onToggleEdit = onEditNameToggle
                    )

                    HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(vertical = 20.dp))

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            //screens title and add screen button
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    "Screens (${screens?.size ?: 0}): ",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Button(onClick = {}) {
                                    Icon(painter = painterResource(id = R.drawable.outline_add_24), contentDescription = "add button")
                                    Text("Add screen")
                                }
                            }

                            //screen card list
                            ScreenList(
                                screens,
                                onDeleteScreenClick = { screenToDelete ->
                                    val originalIndex = screens?.indexOf(screenToDelete) ?: -1
                                    //hide the screen from ui
                                    onDeleteScreenClick(screenToDelete)

                                    scope.launch {
                                        var actionHandled = false

                                        try {
                                            val result = snackbarHostState.showSnackbar(
                                                message = "Screen deleted",
                                                actionLabel = "Undo",
                                                duration = SnackbarDuration.Short
                                            )

                                            when (result) {
                                                // user clicked UNDO
                                                SnackbarResult.ActionPerformed -> {
                                                    onUndoDeleteScreen(screenToDelete, originalIndex)
                                                    actionHandled = true
                                                }
                                                // snackbar disappeared - send DELETE http request
                                                SnackbarResult.Dismissed -> {
                                                    onConfirmDeleteScreen(screenToDelete, originalIndex)
                                                    actionHandled = true
                                                }
                                            }
                                        } finally {
                                            //if the back button is pressed while waiting for undo
                                            if (!actionHandled) onConfirmDeleteScreen(screenToDelete, originalIndex)
                                        }
                                    }

                                },
                                onMove = onScreenItemMove,
                                onDragEnd = onScreenReorderConfirm
                            )
                        }
                        //list of screens
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    //device hash display
                    DeviceHashDisplay(deviceHash)

                }
            }
        }
    }

    //show delete device dialog if needed
    if (showDeleteDeviceDialog) {
        DeleteDeviceDialog(
            onConfirm = {
                showDeleteDeviceDialog = false
                onDeleteClick()
            },
            onDismiss = {showDeleteDeviceDialog = false }
        )
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark mode")
fun DeviceCreatePreview() {
    val screens: List<MarketViewerScreen> = listOf(
        CryptoScreen(1, 1, "bro", "", "", "", displayGraph = false, simpleDisplay = false),
        CryptoScreen(2, 1, "bro", "", "", "", displayGraph = false, simpleDisplay = false),
        ClockScreen(3, 1, "bro", "", "", ""),
        ClockScreen(4, 1, "bro", "", "", ""),
        ClockScreen(5, 1, "bro", "", "", ""),
        ClockScreen(6, 1, "bro", "", "", ""),
        ClockScreen(7, 1, "bro", "", "", ""),
        ClockScreen(8, 1, "bro", "", "", ""),
        ClockScreen(9, 1, "bro", "", "", ""),
        ClockScreen(10, 1, "bro", "", "", ""),
    )

    MarketViewerTheme {
        DeviceDetailScreenContent(
            deviceId = 10,
            isLoading = false,
            deviceName = "Office device my office at homebecuse i like my off ice ",
            deviceHash = "sdf45-564dg65df-45d6fg",
            errorMsg = null,
            nameChangeErrorMsg = null,
            isEditingName = false,
            screens = screens,
            onDeleteClick = {},
            onBackClicked = {},
            onUndoDeleteScreen = {screen, index -> },
            onDeleteScreenClick = {},
            onConfirmDeleteScreen = {screen, index -> },
            onScreenReorderConfirm = {},
            onScreenItemMove = {from, to -> },
            onDeviceNameChange = {},
            onEditNameToggle = {}

        )
    }
}