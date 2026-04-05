package cz.cvut.fel.zan.marketviewer.feature.devices.domain.repository

import cz.cvut.fel.zan.marketviewer.core.domain.ApiResult
import cz.cvut.fel.zan.marketviewer.feature.devices.data.remote.dto.DeviceCreateResponseDto
import cz.cvut.fel.zan.marketviewer.feature.devices.domain.model.MarketViewerDevice

interface DeviceRepository {
    suspend fun listDevices() : ApiResult<List<MarketViewerDevice>>

    suspend fun createDevice(deviceName: String) : ApiResult<DeviceCreateResponseDto>
    suspend fun deleteDevice(deviceId: Int) : ApiResult<Unit>
}