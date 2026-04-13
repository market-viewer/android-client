package cz.cvut.fel.zan.marketviewer.feature.screens.domain.model

data class CryptoScreen(
    override val id: Int,
    override val position: Int,
    override val screenType: ScreenType = ScreenType.CRYPTO,
    val assetName: String,
    val timeFrame: CryptoTimeFrame,
    val currency: String,
    val graphType: GraphType,
    val displayGraph: Boolean,
    val simpleDisplay: Boolean
) : MarketViewerScreen