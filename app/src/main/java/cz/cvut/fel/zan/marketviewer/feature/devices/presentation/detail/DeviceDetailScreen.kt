package cz.cvut.fel.zan.marketviewer.feature.devices.presentation.detail

import android.content.ClipData
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.cvut.fel.zan.marketviewer.R
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.AITextScreen
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.ClockScreen
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.CryptoScreen
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.MarketViewerScreen
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.StockScreen
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.TimerScreen
import cz.cvut.fel.zan.marketviewer.feature.screens.presentation.card.AITextScreenCard
import cz.cvut.fel.zan.marketviewer.feature.screens.presentation.card.ClockScreenCard
import cz.cvut.fel.zan.marketviewer.feature.screens.presentation.card.CryptoScreenCard
import cz.cvut.fel.zan.marketviewer.feature.screens.presentation.card.ScreenListCard
import cz.cvut.fel.zan.marketviewer.feature.screens.presentation.card.StockScreenCard
import cz.cvut.fel.zan.marketviewer.feature.screens.presentation.card.TimerScreenCard
import io.ktor.serialization.Configuration
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
                    DeviceNameTitle(deviceName)

                    ScreenList(screens)

                    Spacer(modifier = Modifier.height(16.dp))

                    DeviceHashDisplay(deviceHash)

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
        text = "Device name:"
    )
    Text(
        text = deviceName ?: "Unknown name",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun ScreenList(
    screens: List<MarketViewerScreen>?
) {
    Box(
        al
    )
    LazyColumn {
        if (screens.isNullOrEmpty()) {
            item{ Text("No Screens") }
        } else {
            items(screens) { screen ->
                when (screen) {
                    is CryptoScreen ->
                        ScreenListCard(
                            screen.position,
                            "Crypto",
                            additionalInfo = "Coin name: ${screen.assetName}",
                            icon = R.drawable.currency_bitcoin_40px
                        )
                    is StockScreen ->
                        ScreenListCard(
                            screen.position,
                            "Stock",
                            additionalInfo = "Stock symbol: ${screen.symbol}",
                            icon = R.drawable.finance_mode_40px
                        )
                    is ClockScreen ->
                        ScreenListCard(
                            screen.position,
                            "Clock",
                            additionalInfo = "Timezone: ${screen.timezone}",
                            icon = R.drawable.nest_clock_farsight_analog_40px
                        )
                    is TimerScreen ->
                        ScreenListCard(
                            screen.position,
                            "Timer",
                            additionalInfo = "Name: ${screen.name}",
                            icon = R.drawable.timer_40px
                        )
                    is AITextScreen ->
                        ScreenListCard(
                            screen.position,
                            "AI text",
                            additionalInfo = "Prompt: ${screen.prompt}",
                            icon = R.drawable.network_intel_node_40px
                        )
                }
            }
        }
    }
}

@Composable
private fun DeviceHashDisplay(
    deviceHash: String?
) {
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }

    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
    ) {
        OutlinedTextField(
            value = deviceHash ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Device hash") },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions( keyboardType = KeyboardType.Password),
            modifier = Modifier.weight(1f).padding(end = 10.dp),
            trailingIcon = {
                IconButton(onClick = {isPasswordVisible = !isPasswordVisible}) {
                    Icon(
                        painter = painterResource(id = if (isPasswordVisible) R.drawable.visibility_off_24px else R.drawable.visibility_24px),
                        contentDescription = "visibility toggle"
                    )
                }
            }
        )

        IconButton(
            onClick = {
                // Only copy if the hash actually exists
                deviceHash?.let { hash ->
                    scope.launch {
                        val clipData = ClipData.newPlainText("Device Hash", hash)
                        // B: Wrap it in the Compose ClipEntry and set it
                        clipboard.setClipEntry(ClipEntry(clipData))
                    }
                }
            },
        ) {
            Icon(
                painter = painterResource(id = R.drawable.content_copy_24px),
                contentDescription = "Copy hash to clipboard",
            )
        }
    }

}


@Composable
@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.)
fun DeviceCreatePreview() {
    DeviceDetailScreenContent(
        deviceId = 10,
        isLoading = false,
        deviceName = "Office device",
        deviceHash = "sdf45-564dg65df-45d6fg",
        errorMsg = null,
        screens = null,
        onDeleteClick = {},
        onBackClicked = {}
    )
}