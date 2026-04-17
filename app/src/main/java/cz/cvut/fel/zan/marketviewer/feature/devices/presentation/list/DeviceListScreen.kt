package cz.cvut.fel.zan.marketviewer.feature.devices.presentation.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.cvut.fel.zan.marketviewer.R
import cz.cvut.fel.zan.marketviewer.core.presentation.components.MarketViewerScaffold
import cz.cvut.fel.zan.marketviewer.feature.devices.domain.model.MarketViewerDevice
import org.koin.androidx.compose.koinViewModel

@Composable
fun DeviceListScreen(
    onNavigateToDeviceDetail: (Int) -> Unit,
    viewModel: DeviceListViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }

    //handle effects
    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is DeviceListViewModel.DeviceListEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
                is DeviceListViewModel.DeviceListEffect.NavigateToDeviceDetail -> onNavigateToDeviceDetail(effect.deviceId)
            }
        }
    }

    DeviceListScreenContent(
        isLoading = state.isLoading,
        errorMsg = state.errorMsg,
        devices = state.devices,
        showCreateDialog = state.showCreateDialog,
        newDeviceName = state.newDeviceName,
        dialogErrorMsg = state.dialogErrorMsg,

        snackBarHostState = snackbarHostState,

        onDeviceClick = { viewModel.onEvent(DeviceListViewModel.DeviceListScreenEvent.DeviceDetailOpen(it)) },
        onLogoutButtonClick = { viewModel.onEvent(DeviceListViewModel.DeviceListScreenEvent.Logout) },
        onRefreshScreen = { viewModel.onEvent(DeviceListViewModel.DeviceListScreenEvent.RefreshScreen) },
        onCreateClick = { viewModel.onEvent(DeviceListViewModel.DeviceListScreenEvent.OpenCreateDialog) },
        onNewDeviceNameChange = { viewModel.onEvent(DeviceListViewModel.DeviceListScreenEvent.NewDeviceNameChanged(it)) },
        onDeviceCreateDismiss = { viewModel.onEvent(DeviceListViewModel.DeviceListScreenEvent.CloseCreateDialog) },
        onDeviceCreateSubmit = { viewModel.onEvent(DeviceListViewModel.DeviceListScreenEvent.CreateDeviceSubmit) },
    )


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceListScreenContent(
    isLoading: Boolean,
    errorMsg: String?,
    devices: List<MarketViewerDevice>,
    showCreateDialog: Boolean,
    newDeviceName: String,
    dialogErrorMsg: String?,

    snackBarHostState: SnackbarHostState,

    onDeviceClick: (Int) -> Unit,
    onRefreshScreen: () -> Unit,
    onLogoutButtonClick: () -> Unit,
    onCreateClick: () -> Unit,
    onNewDeviceNameChange: (String) -> Unit,
    onDeviceCreateDismiss: () -> Unit,
    onDeviceCreateSubmit: () -> Unit
) {
    MarketViewerScaffold (
        //top app bar
        topBar = {
            TopAppBar(
                title = { Text("My devices", fontWeight = FontWeight.Bold) },
                windowInsets = WindowInsets(0.dp),
                actions = {
                    TextButton(onClick = onLogoutButtonClick) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.outline_logout_24),
                                contentDescription = "Logout",
                            )
                            Text("Logout")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateClick,
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_add_50),
                    contentDescription = "addDevice",
                    modifier = Modifier.padding(5.dp)
                )
            }
        },
        snackbarHostState = snackBarHostState,
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading && devices.isEmpty()) {
                CircularProgressIndicator()
            }
            // show error message with retry button
            else if (errorMsg != null && devices.isEmpty()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = errorMsg,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal=16.dp).padding(top=16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onRefreshScreen
                    ) {
                        Text("Retry")
                    }
                }
            }
            // success state - show the list wrapped in PullToRefreshBox
            else {
                DeviceListPullToRefresh(
                    isLoading = isLoading,
                    devices = devices,
                    onRefreshScreen = onRefreshScreen,
                    onDeviceClick = onDeviceClick
                )
            }
        }

        if (showCreateDialog) {
            CreateDeviceDialog(
                deviceName = newDeviceName,
                errorMsg = dialogErrorMsg,
                onNameChange = onNewDeviceNameChange,
                onDismiss = onDeviceCreateDismiss,
                onCreateConfirm = onDeviceCreateSubmit
            )
        }

    }
}

@Composable
fun DeviceListPullToRefresh(
    isLoading: Boolean,
    devices: List<MarketViewerDevice>,
    onRefreshScreen: () -> Unit,
    onDeviceClick: (Int) -> Unit
) {
    PullToRefreshBox(
        isRefreshing = isLoading,
        onRefresh = onRefreshScreen,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            if (devices.isEmpty()) {
                item {
                    Text(
                        text = "No devices found. Pull down to refresh.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                items(devices) { device ->
                    DeviceItemCard(device = device, onDeviceClick = { onDeviceClick(device.id) })
                }

                item {
                    HorizontalDivider(thickness = 2.dp, modifier = Modifier.padding(bottom = 16.dp))
                    Text(
                        text = "Device count: ${devices.size}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun DeviceListScreenPreview() {
    val devicesPreview = listOf(
        MarketViewerDevice(1, "Workshop device", 5),
        MarketViewerDevice(2, "Living room", 3),
        MarketViewerDevice(3, "Office", 1),
    )


    DeviceListScreenContent(
        isLoading = false,
        errorMsg = null,
        devices = devicesPreview,
        newDeviceName = "",
        showCreateDialog = false,
        dialogErrorMsg = "Invalid device name",
        snackBarHostState = SnackbarHostState(),
        onDeviceClick = {},
        onRefreshScreen = {},
        onLogoutButtonClick = {},
        onDeviceCreateSubmit = {},
        onDeviceCreateDismiss = {},
        onNewDeviceNameChange = {},
        onCreateClick = {}
    )
}