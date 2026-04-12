package cz.cvut.fel.zan.marketviewer.feature.screens.domain.model

sealed interface MarketViewerScreen {
    val id: Int
    val position: Int
    val screenType: ScreenType
}
