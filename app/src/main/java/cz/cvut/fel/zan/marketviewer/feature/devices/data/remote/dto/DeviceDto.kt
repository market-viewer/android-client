package cz.cvut.fel.zan.marketviewer.feature.devices.data.remote.dto

import cz.cvut.fel.zan.marketviewer.feature.devices.domain.model.MarketViewerDevice
import io.ktor.util.collections.StringMap
import kotlinx.serialization.Serializable

@Serializable
data class DeviceDto(
    val id: Int,
    val name: String,
    val screenCount: Int?
)

fun DeviceDto.toDomain() : MarketViewerDevice {
    return MarketViewerDevice(
        id = this.id,
        name = this.name,
        screenCount = this.screenCount ?: 0
    )
}