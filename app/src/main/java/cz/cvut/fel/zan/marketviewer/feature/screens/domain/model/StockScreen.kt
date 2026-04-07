package cz.cvut.fel.zan.marketviewer.feature.screens.domain.model

data class StockScreen(
    override val id: Int,
    override val position: Int,
    val symbol: String,
    val timeFrame: String,
    val displayGraph: Boolean,
    val graphType: String,
    val simpleDisplay: Boolean,
    val fetchInterval: Int
) : MarketViewerScreen