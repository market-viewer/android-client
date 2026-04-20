package cz.cvut.fel.zan.marketviewer.feature.screens.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ScreensDeleteRequest(
    val screenIds: Set<Int>
)
