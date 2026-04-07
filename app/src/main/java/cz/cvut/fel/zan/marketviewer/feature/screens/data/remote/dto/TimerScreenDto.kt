package cz.cvut.fel.zan.marketviewer.feature.screens.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
@SerialName("TIMER")
data class TimerScreenDto(
    override val id: Int,
    override val position: Int,
    val name: String
) : ScreenDto()