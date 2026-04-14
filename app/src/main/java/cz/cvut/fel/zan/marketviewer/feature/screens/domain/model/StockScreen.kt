package cz.cvut.fel.zan.marketviewer.feature.screens.domain.model

data class StockScreen(
    override val id: Int,
    override val position: Int,
    override val screenType: ScreenType = ScreenType.STOCK,
    val symbol: String,
    val timeFrame: StockTimeframe,
    val displayGraph: Boolean,
    val graphType: GraphType,
    val simpleDisplay: Boolean,
    val fetchInterval: Int
) : MarketViewerScreen