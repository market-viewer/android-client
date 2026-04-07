package cz.cvut.fel.zan.marketviewer.feature.screens.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("STOCK")
data class StockScreenDto(
    override val id: Int,
    override val position: Int,
    val symbol: String,
    val timeFrame: String,
    val displayGraph: Boolean,
    val graphType: String,
    val simpleDisplay: Boolean,
    val fetchInterval: Int
) : ScreenDto()