package cz.cvut.fel.zan.marketviewer.feature.devices.presentation.detail

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
        screens = state.screens,
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
    screens: List<MarketViewerScreen>?,
    onDeleteClick: () -> Unit,
    onBackClicked: () -> Unit
) {
    //scroll state for whole screen
    val screenScrollState = rememberScrollState()

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
                    modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(screenScrollState),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    //device name
                    DeviceNameTitle(deviceName)

                    HorizontalDivider(thickness = 2.dp, modifier = Modifier.padding(vertical = 20.dp))

                    //device screens
                    Text(
                        "Screens:",
                        modifier = Modifier
                            .padding(bottom=16.dp)
                            .fillMaxWidth(),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        ScreenList(screens)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    //device hash display
                    DeviceHashDisplay(deviceHash)

                    //delete button
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
fun DeviceNameTitle(deviceName: String?) {
    Text(
        text = "Device name:",
        color = MaterialTheme.colorScheme.secondary
    )
    Text(
        text = deviceName ?: "Unknown name",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold
    )
}

@Composable
@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark mode")
fun DeviceCreatePreview() {
    val screens: List<MarketViewerScreen> = listOf(
        CryptoScreen(1, 1, "bro", "", "", "", displayGraph = false, simpleDisplay = false),
        CryptoScreen(1, 1, "bro", "", "", "", displayGraph = false, simpleDisplay = false),
        ClockScreen(1, 1, "bro", "", "", ""),
    )

    MarketViewerTheme {
        DeviceDetailScreenContent(
            deviceId = 10,
            isLoading = false,
            deviceName = "Office device",
            deviceHash = "sdf45-564dg65df-45d6fg",
            errorMsg = null,
            screens = screens,
            onDeleteClick = {},
            onBackClicked = {}
        )
    }
}