package cz.cvut.fel.zan.marketviewer.feature.screens.domain.model

data class TimerScreen(
    override val id: Int,
    override val position: Int,
    override val screenType: ScreenType = ScreenType.TIMER,
    val name: String
) : MarketViewerScreen