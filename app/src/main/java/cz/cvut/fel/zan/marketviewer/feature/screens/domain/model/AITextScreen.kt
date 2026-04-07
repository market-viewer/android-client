package cz.cvut.fel.zan.marketviewer.feature.screens.domain.model

data class AITextScreen(
    override val id: Int,
    override val position: Int,
    val prompt: String,
    val fetchIntervalHours: Int
) : MarketViewerScreen