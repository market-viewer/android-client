package cz.cvut.fel.zan.marketviewer.feature.screens.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("AI_TEXT")
data class AITextScreenDto(
    override val id: Int,
    override val position: Int,
    val prompt: String,
    val fetchIntervalHours: Int
) : ScreenDto()