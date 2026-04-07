package cz.cvut.fel.zan.marketviewer.feature.screens.domain.model

data class TimerScreen(
    override val id: Int,
    override val position: Int,
    val name: String
) : MarketViewerScreen