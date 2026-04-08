package cz.cvut.fel.zan.marketviewer.feature.screens.domain.model

data class CryptoScreen(
    override val id: Int,
    override val position: Int,
    val assetName: String,
    val timeFrame: String,
    val currency: String,
    val graphType: String,
    val displayGraph: Boolean,
    val simpleDisplay: Boolean
) : MarketViewerScreen