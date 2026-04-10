package cz.cvut.fel.zan.marketviewer.feature.devices.presentation.detail

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
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
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun ScreenList(
    screens: List<MarketViewerScreen>?,
    onDeleteScreenClick: (MarketViewerScreen) -> Unit,
    onMove: (fromIndex: Int, toIndex: Int) -> Unit,
    onDragEnd: () -> Unit
) {
    if (screens.isNullOrEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No screens :(", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    } else {
        //snap items in the list
        val listState = rememberLazyListState()
        val hapticFeedback = LocalHapticFeedback.current
        val reorderableState = rememberReorderableLazyListState(listState) {from, to ->
            onMove(from.index, to.index)
            hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            state = listState,
            flingBehavior = rememberSnapFlingBehavior(lazyListState = listState),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items = screens, key = {it.id}) { screen ->

                //make items reorder available
                ReorderableItem(reorderableState, key = screen.id) { isDragging ->
                    val elevation by animateDpAsState(if (isDragging) 4.dp else 0.dp)

                    Box(
                        modifier = Modifier
                            .longPressDraggableHandle(
                                onDragStarted = {
                                    hapticFeedback.performHapticFeedback(
                                        HapticFeedbackType.GestureThresholdActivate
                                    )
                                },
                                onDragStopped = {
                                    hapticFeedback.performHapticFeedback(
                                        HapticFeedbackType.GestureEnd
                                    )
                                    onDragEnd()
                                }
                            )
                            .graphicsLayer { shadowElevation = elevation.toPx() }
                    ) {
                        val liveIndex = screens.indexOf(screen)

                        when (screen) {
                            is CryptoScreen ->
                                ScreenListCard(
                                    liveIndex,
                                    "Crypto",
                                    additionalInfo = screen.assetName,
                                    icon = R.drawable.currency_bitcoin_40px,
                                    onDeleteClick = { onDeleteScreenClick(screen) }

                                )
                            is StockScreen ->
                                ScreenListCard(
                                    liveIndex,
                                    "Stock",
                                    additionalInfo = screen.symbol,
                                    icon = R.drawable.finance_mode_40px,
                                    onDeleteClick = { onDeleteScreenClick(screen) }
                                )
                            is ClockScreen ->
                                ScreenListCard(
                                    liveIndex,
                                    "Clock",
                                    additionalInfo = screen.timezone,
                                    icon = R.drawable.nest_clock_farsight_analog_40px,
                                    onDeleteClick = { onDeleteScreenClick(screen) }
                                )
                            is TimerScreen ->
                                ScreenListCard(
                                    liveIndex,
                                    "Timer",
                                    additionalInfo = "Name: ${screen.name}",
                                    icon = R.drawable.timer_40px,
                                    onDeleteClick = { onDeleteScreenClick(screen) }
                                )
                            is AITextScreen ->
                                ScreenListCard(
                                    liveIndex,
                                    "AI text",
                                    additionalInfo = "Prompt: ${screen.prompt}",
                                    icon = R.drawable.network_intel_node_40px,
                                    onDeleteClick = { onDeleteScreenClick(screen) }
                                )
                        }
                    }
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
        ScreenList(emptyList(), { screen -> Unit}, {from, to ->}, {})
    }
}