package cz.cvut.fel.zan.marketviewer.feature.screens.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("CRYPTO")
data class CryptoScreenDto(
    override val id: Int,
    override val position: Int,
    val assetName: String,
    val timeFrame: String,
    val currency: String,
    val graphType: String,
    val displayGraph: Boolean,
    val simpleDisplay: Boolean

) : ScreenDto()