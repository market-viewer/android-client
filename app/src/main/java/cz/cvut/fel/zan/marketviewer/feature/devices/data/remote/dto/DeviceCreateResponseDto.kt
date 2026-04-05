package cz.cvut.fel.zan.marketviewer.feature.devices.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class DeviceCreateResponseDto(
    val deviceId: Int
)
