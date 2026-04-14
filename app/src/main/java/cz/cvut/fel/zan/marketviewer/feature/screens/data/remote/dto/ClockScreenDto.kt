package cz.cvut.fel.zan.marketviewer.feature.screens.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("CLOCK")
data class ClockScreenDto(
    override val id: Int,
    override val position: Int,
    val timezone: String,
    val clockType: String,
    val timeFormat: String
) : ScreenDto()