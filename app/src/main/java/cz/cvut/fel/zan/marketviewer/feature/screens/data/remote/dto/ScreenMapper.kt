package cz.cvut.fel.zan.marketviewer.feature.screens.data.remote.dto

import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.AITextScreen
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.ClockScreen
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.CryptoScreen
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.CryptoTimeFrame
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.GraphType
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.MarketViewerScreen
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.StockScreen
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.TimerScreen

fun ScreenDto.toDomain(): MarketViewerScreen {
    return when (this) {
        is AITextScreenDto -> AITextScreen(
            id = this.id,
            position = this.position,
            prompt = this.prompt,
            fetchIntervalHours = this.fetchIntervalHours
        )
        is ClockScreenDto -> ClockScreen(
            id = this.id,
            position = this.position,
            timezone = this.timezone,
            timezoneCode = this.timezoneCode,
            clockType = this.clockType,
            timeFormat = this.timeFormat
        )
        is CryptoScreenDto -> CryptoScreen(
            id = this.id,
            position = this.position,
            assetName = this.assetName,
            timeFrame = CryptoTimeFrame.fromString(this.timeFrame),
            currency = this.currency,
            graphType = GraphType.fromString(this.graphType),
            displayGraph = this.displayGraph,
            simpleDisplay = this.simpleDisplay
        )
        is StockScreenDto -> StockScreen(
            id = this.id,
            position = this.position,
            symbol = this.symbol,
            timeFrame = this.timeFrame,
            displayGraph = this.displayGraph,
            graphType = this.graphType,
            simpleDisplay = this.simpleDisplay,
            fetchInterval = this.fetchInterval
        )
        is TimerScreenDto -> TimerScreen(
            id = this.id,
            position = this.position,
            name = this.name
        )
    }
}

fun MarketViewerScreen.toDto(): ScreenDto {
    return when (this) {
        is AITextScreen -> AITextScreenDto(
            id = this.id,
            position = this.position,
            prompt = this.prompt,
            fetchIntervalHours = this.fetchIntervalHours
        )
        is ClockScreen -> ClockScreenDto(
            id = this.id,
            position = this.position,
            timezone = this.timezone,
            timezoneCode = this.timezoneCode,
            clockType = this.clockType,
            timeFormat = this.timeFormat
        )
        is CryptoScreen -> CryptoScreenDto(
            id = this.id,
            position = this.position,
            assetName = this.assetName,
            timeFrame = this.timeFrame.label,
            currency = this.currency,
            graphType = this.graphType.name,
            displayGraph = this.displayGraph,
            simpleDisplay = this.simpleDisplay
        )
        is StockScreen -> StockScreenDto(
            id = this.id,
            position = this.position,
            symbol = this.symbol,
            timeFrame = this.timeFrame,
            displayGraph = this.displayGraph,
            graphType = this.graphType,
            simpleDisplay = this.simpleDisplay,
            fetchInterval = this.fetchInterval
        )
        is TimerScreen -> TimerScreenDto(
            id = this.id,
            position = this.position,
            name = this.name
        )
    }
}