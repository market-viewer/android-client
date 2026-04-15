package cz.cvut.fel.zan.marketviewer.feature.screens.data.local

import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.*

fun ScreenEntity.toDomain(): MarketViewerScreen {
    return when (this.screenType) {
        "CRYPTO" -> CryptoScreen(
            id = id,
            position = position,
            assetName = assetName!!,
            timeFrame = CryptoTimeframe.fromString(timeFrame!!),
            currency = currency!!,
            graphType = GraphType.fromString(graphType!!),
            displayGraph = displayGraph!!,
            simpleDisplay = simpleDisplay!!,
            fetchInterval = fetchInterval!!
        )
        "STOCK" -> StockScreen(
            id = id,
            position = position,
            symbol = assetName!!,
            timeFrame = StockTimeframe.fromString(timeFrame!!),
            displayGraph = displayGraph!!,
            graphType = GraphType.fromString(graphType!!),
            simpleDisplay = simpleDisplay!!,
            fetchInterval = fetchInterval!!
        )
        "CLOCK" -> ClockScreen(
            id = id,
            position = position,
            timezone = timezone!!,
            clockType = ClockType.fromString(clockType!!),
            timeFormat = ClockTimeFormat.fromString(timeFormat!!)
        )
        "TIMER" -> TimerScreen(
            id = id,
            position = position,
            name = timerName!!
        )
        "AI_TEXT" -> AITextScreen(
            id = id,
            position = position,
            prompt = prompt!!,
            fetchIntervalHours = fetchIntervalHours!!
        )
        else -> throw IllegalArgumentException("Unknown screen type in DB: $screenType")
    }
}

fun MarketViewerScreen.toEntity(deviceId: Int): ScreenEntity {
    return when (this) {
        is CryptoScreen -> ScreenEntity(
            id = id,
            deviceId = deviceId,
            position = position,
            screenType = "CRYPTO",
            assetName = assetName,
            timeFrame = timeFrame.label,
            currency = currency,
            graphType = graphType.name,
            displayGraph = displayGraph,
            simpleDisplay = simpleDisplay,
            fetchInterval = fetchInterval
        )
        is StockScreen -> ScreenEntity(
            id = id,
            deviceId = deviceId,
            position = position,
            screenType = "STOCK",
            assetName = symbol,
            timeFrame = timeFrame.label,
            graphType = graphType.label,
            displayGraph = displayGraph,
            simpleDisplay = simpleDisplay,
            fetchInterval = fetchInterval
        )
        is ClockScreen -> ScreenEntity(
            id = id,
            deviceId = deviceId,
            position = position,
            screenType = "CLOCK",
            timezone = timezone,
            clockType = clockType.name,
            timeFormat = timeFormat.label
        )
        is TimerScreen -> ScreenEntity(
            id = id,
            deviceId = deviceId,
            position = position,
            screenType = "TIMER",
            timerName = name
        )
        is AITextScreen -> ScreenEntity(
            id = id,
            deviceId = deviceId,
            position = position,
            screenType = "AI_TEXT",
            prompt = prompt,
            fetchIntervalHours = fetchIntervalHours
        )
    }
}