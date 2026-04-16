package cz.cvut.fel.zan.marketviewer.feature.devices.data.local

import cz.cvut.fel.zan.marketviewer.feature.devices.domain.model.MarketViewerDevice

fun MarketViewerDevice.toEntity(): DeviceEntity {
    return DeviceEntity(
        id = this.id,
        name = this.name,
        screenCount = this.screenCount
    )
}

fun DeviceEntity.toDomain(): MarketViewerDevice {
    return MarketViewerDevice(
        id = this.id,
        name = this.name,
        screenCount = this.screenCount
    )
}