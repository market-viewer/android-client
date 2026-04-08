package cz.cvut.fel.zan.marketviewer.feature.devices.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.cvut.fel.zan.marketviewer.R
import cz.cvut.fel.zan.marketviewer.core.presentation.theme.MarketViewerTheme
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.AITextScreen
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.ClockScreen
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.CryptoScreen
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.MarketViewerScreen
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.StockScreen
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.TimerScreen
import cz.cvut.fel.zan.marketviewer.feature.screens.presentation.card.ScreenListCard

@Composable
fun ScreenList(
    screens: List<MarketViewerScreen>?
) {
    if (screens.isNullOrEmpty()) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No screens", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.secondary)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
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

@Preview
@Composable
fun DeviceListPreview() {
    val screens: List<MarketViewerScreen> = listOf(
        CryptoScreen(1, 1, "bro", "", "", "", displayGraph = false, simpleDisplay = false),
        CryptoScreen(1, 1, "bro", "", "", "", displayGraph = false, simpleDisplay = false),
        ClockScreen(1, 1, "bro", "", "", ""),
    )

    MarketViewerTheme {
        ScreenList(emptyList())
    }
}