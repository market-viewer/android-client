package cz.cvut.fel.zan.marketviewer.feature.devices.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class MarketViewerDevice(
    val id: Int,
    val name: String,
    val screenCount: Int
)
