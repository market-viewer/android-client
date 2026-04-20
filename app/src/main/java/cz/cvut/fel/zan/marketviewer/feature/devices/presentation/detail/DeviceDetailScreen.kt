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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.TopAppBarDefaults
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
import cz.cvut.fel.zan.marketviewer.core.presentation.components.MarketViewerScaffold
import cz.cvut.fel.zan.marketviewer.core.presentation.components.MarketViewerTopAppBar
import cz.cvut.fel.zan.marketviewer.core.presentation.theme.MarketViewerTheme
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.CryptoScreen
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.CryptoTimeframe
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.GraphType
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.MarketViewerScreen
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.ScreenType
import cz.cvut.fel.zan.marketviewer.feature.screens.presentation.add.ScreenAddDialog
import cz.cvut.fel.zan.marketviewer.feature.screens.presentation.edit.ScreenConfigBottomSheet
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun DeviceDetailScreen(
    onBackClicked: () -> Unit,
    onDrawerOpen: () -> Unit,
    viewModel: DeviceDetailViewModel = koinViewModel()
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is DeviceDetailViewModel.DeviceDetailEffect.GoBackToDeviceList -> {
                    onBackClicked()
                }
                is DeviceDetailViewModel.DeviceDetailEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    DeviceDetailScreenContent(
        deviceId = state.deviceId,
        isLoading = state.isLoading,
        deviceName = state.deviceName,
        deviceHash = state.deviceHash,
        nameChangeErrorMsg = state.nameChangeErrorMsg,
        isEditingName = state.isEditingName,
        screens = state.screens,
        isScreenAddDialogVisible = state.showAddScreenDialog,
        screenAddErrorMsg = state.screenAddErrorMsg,
        snackbarHostState = snackbarHostState,

        selectedScreenIds = state.selectedScreenIds,

        onEditNameToggle = { viewModel.onEvent(DeviceDetailViewModel.DeviceDetailEvents.ToggleNameEdit) },
        onDeleteClick = { viewModel.onEvent(DeviceDetailViewModel.DeviceDetailEvents.DeleteDeviceClick) },
        onBackClicked = onBackClicked,

        onToggleScreenSelection = { viewModel.onEvent(DeviceDetailViewModel.DeviceDetailEvents.ToggleScreenSelection(it)) },
        onClearScreenSelection = { viewModel.onEvent(DeviceDetailViewModel.DeviceDetailEvents.ClearScreenSelection) },
        onDeleteSelectedScreens = { viewModel.onEvent(DeviceDetailViewModel.DeviceDetailEvents.DeleteSelectedScreens) },

        onScreenItemMove = { fromIndex, toIndex -> viewModel.onEvent(DeviceDetailViewModel.DeviceDetailEvents.ReorderScreenLocally(fromIndex, toIndex)) },
        onScreenReorderConfirm = { viewModel.onEvent(DeviceDetailViewModel.DeviceDetailEvents.ConfirmReorderScreens) },
        onDeviceNameChange = { viewModel.onEvent(DeviceDetailViewModel.DeviceDetailEvents.ChangeDeviceName(it)) },
        hideScreenAddDialog = { viewModel.onEvent(DeviceDetailViewModel.DeviceDetailEvents.ToggleScreenAddDialog(false)) },
        onShowScreenAddDialog = { viewModel.onEvent(DeviceDetailViewModel.DeviceDetailEvents.ToggleScreenAddDialog(true)) },
        onScreenAdd = { viewModel.onEvent(DeviceDetailViewModel.DeviceDetailEvents.AddScreenEvent(it)) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceDetailScreenContent(
    deviceId: Int,
    isLoading: Boolean,
    deviceName: String?,
    deviceHash: String?,
    nameChangeErrorMsg: String?,
    isEditingName: Boolean,
    snackbarHostState: SnackbarHostState,
    screens: List<MarketViewerScreen>?,
    isScreenAddDialogVisible: Boolean,
    screenAddErrorMsg: String?,
    selectedScreenIds: Set<Int>,
    onEditNameToggle: () -> Unit,
    onDeleteClick: () -> Unit,
    onBackClicked: () -> Unit,
    onToggleScreenSelection: (Int) -> Unit,
    onClearScreenSelection: () -> Unit,     // <-- Added
    onDeleteSelectedScreens: () -> Unit,    // <-- Added
    onScreenItemMove: (fromIndex: Int, toIndex: Int) -> Unit,
    onScreenReorderConfirm: () -> Unit,
    onDeviceNameChange: (String) -> Unit,
    hideScreenAddDialog: () -> Unit,
    onShowScreenAddDialog: () -> Unit,
    onScreenAdd: (ScreenType) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDeviceDialog by remember { mutableStateOf(false) }
    var showBulkDeleteDialog by remember { mutableStateOf(false) }
    var showScreenConfigSheet by remember { mutableStateOf(false) }
    var screenToEdit by remember { mutableStateOf<MarketViewerScreen?>(null) }

    val isSelectionMode = selectedScreenIds.isNotEmpty()

    MarketViewerScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    if (isSelectionMode) Text("${selectedScreenIds.size} Selected")
                    else Text("Device detail")
                },
                windowInsets = WindowInsets(0.dp),
                // change color for selection mode
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isSelectionMode) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface,
                    titleContentColor = if (isSelectionMode) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        if (isSelectionMode) onClearScreenSelection() else onBackClicked()
                    }) {
                        Icon(
                            painter = painterResource(id = if (isSelectionMode) R.drawable.close_24px else R.drawable.outline_arrow_back_24),
                            contentDescription = if (isSelectionMode) "Clear selection" else "Back"
                        )
                    }
                },
                actions = {
                    if (isSelectionMode) {
                        IconButton(onClick = { showBulkDeleteDialog = true }) {
                            Icon(
                                painter = painterResource(id = R.drawable.outline_delete_24),
                                contentDescription = "Delete Selected",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    } else {
                        TextButton(onClick = { showDeleteDeviceDialog = true }) {
                            Icon(
                                painter = painterResource(id = R.drawable.outline_delete_forever_24),
                                contentDescription = "Delete icon",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(end = 5.dp)
                            )
                            Text("Delete", color = MaterialTheme.colorScheme.error)
                        }
                    }
                },
            )
        },
        snackbarHostState = snackbarHostState
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            }
            else {
                Column(
                    modifier = Modifier.fillMaxSize().padding(horizontal=16.dp).padding(top=16.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DeviceNameTitle(
                        deviceName = deviceName,
                        isEditing = isEditingName,
                        nameChangeErrorMsg = nameChangeErrorMsg,
                        onNameChangeSave = onDeviceNameChange,
                        onToggleEdit = onEditNameToggle
                    )

                    HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(vertical = 20.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)
                            ) {
                                Text("Screens (${screens?.size ?: 0}): ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                                Button(
                                    onClick = onShowScreenAddDialog,
                                    enabled = !isSelectionMode
                                ) {
                                    Icon(painter = painterResource(id = R.drawable.outline_add_24), contentDescription = "add button")
                                    Text("Add screen")
                                }
                            }

                            ScreenList(
                                screens = screens,
                                selectedScreenIds = selectedScreenIds,
                                onToggleSelection = onToggleScreenSelection,
                                onMove = onScreenItemMove,
                                onDragEnd = onScreenReorderConfirm,
                                onScreenEditClick = {
                                    screenToEdit = it
                                    showScreenConfigSheet = true
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    DeviceHashDisplay(deviceHash)

                    if (showScreenConfigSheet && screenToEdit != null) {
                        ScreenConfigBottomSheet(
                            deviceId = deviceId,
                            screenToEdit = screenToEdit!!,
                            onDismiss = { showScreenConfigSheet = false },
                        )
                    }
                }
            }
        }
    }


    //dialogs
    if (showBulkDeleteDialog) {
        ScreenDeleteDialog(
            onDismiss = { showBulkDeleteDialog = false },
            selectedCount = selectedScreenIds.size,
            onConfirm = {
                showBulkDeleteDialog = false
                onDeleteSelectedScreens()
                onClearScreenSelection()
            }
        )
    }

    if (isScreenAddDialogVisible) {
        ScreenAddDialog(
            onDismiss = hideScreenAddDialog,
            onConfirm = { onScreenAdd(it) },
            errorMsg = screenAddErrorMsg
        )
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
fun DeviceDetailPreview() {
    val screens: List<MarketViewerScreen> = listOf(
        CryptoScreen(id = 1, position = 1, assetName = "bro",  timeFrame = CryptoTimeframe.DAY, currency = "", graphType = GraphType.LINE, displayGraph = false, simpleDisplay = false, fetchInterval = 10),
    )

    MarketViewerTheme {
        DeviceDetailScreenContent(
            deviceId = 10,
            isLoading = false,
            deviceName = "Office device my office at home becuse i like my off ice ",
            deviceHash = "sdf45-564dg65df-45d6fg",
            nameChangeErrorMsg = null,
            isEditingName = false,
            screens = screens,
            isScreenAddDialogVisible = false,
            screenAddErrorMsg = null,
            snackbarHostState = SnackbarHostState(),

            // Standard mode: empty selection
            selectedScreenIds = setOf(1, 2),

            onEditNameToggle = {},
            onDeleteClick = {},
            onBackClicked = {},

            // Multi-select events
            onToggleScreenSelection = {},
            onClearScreenSelection = {},
            onDeleteSelectedScreens = {},

            onScreenItemMove = { _, _ -> },
            onScreenReorderConfirm = {},
            onDeviceNameChange = {},
            hideScreenAddDialog = {},
            onShowScreenAddDialog = {},
            onScreenAdd = {},
        )
    }
}