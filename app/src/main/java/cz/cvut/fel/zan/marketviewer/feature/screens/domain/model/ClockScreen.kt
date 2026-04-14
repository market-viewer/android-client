package cz.cvut.fel.zan.marketviewer.feature.screens.domain.model

data class ClockScreen(
    override val id: Int,
    override val position: Int,
    override val screenType: ScreenType = ScreenType.CLOCK,
    val timezone: String,
    val clockType: ClockType,
    val timeFormat: ClockTimeFormat
) : MarketViewerScreen