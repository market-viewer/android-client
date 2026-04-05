package cz.cvut.fel.zan.marketviewer.feature.devices.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun DeviceDetailScreen(
    onBackClicked: () -> Unit,
    viewModel: DeviceDetailViewModel = koinViewModel()
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is DeviceDetailViewModel.DeviceDetailEffect.GoBackToDeviceList -> {
                    onBackClicked()
                }
            }
        }
    }


    DeviceDetailScreenContent(
        deviceId = state.deviceId,
        onDeleteClick = { viewModel.onEvent(DeviceDetailViewModel.DeviceDetailEvents.DeleteDeviceClick) }
    )
}

@Composable
fun DeviceDetailScreenContent(
    deviceId: Int,
    onDeleteClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Device id: $deviceId")
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

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun DeviceCreatePreview() {
    DeviceDetailScreenContent(10, {})
}