package cz.cvut.fel.zan.marketviewer.feature.devices.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.cvut.fel.zan.marketviewer.R
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
        onDeleteClick = { viewModel.onEvent(DeviceDetailViewModel.DeviceDetailEvents.DeleteDeviceClick) },
        onBackClicked = onBackClicked
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
    onDeleteClick: () -> Unit,
    onBackClicked: () -> Unit
) {
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
                windowInsets = WindowInsets(0.dp),
            )
        }
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
                    Text(
                        text = deviceName ?: "Unknown name",
                        style = MaterialTheme.typography.titleLarge,
                        textDecoration = TextDecoration.Underline
                    )
                    Button(
                        onClick = onDeleteClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError,
                        )
                    ) {
                        Text("Delete device")
                    }
                }
            }
        }
    }


}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun DeviceCreatePreview() {
    DeviceDetailScreenContent(
        deviceId = 10,
        isLoading = false,
        deviceName = "Office device",
        deviceHash = null,
        errorMsg = null,
        onDeleteClick = {},
        onBackClicked = {}
    )
}