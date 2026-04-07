package cz.cvut.fel.zan.marketviewer.feature.screens.data.remote.dto

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator


@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("screenType") //decide which screen is it based on screenType value
sealed class ScreenDto {
    abstract val id: Int
    abstract val position: Int
}