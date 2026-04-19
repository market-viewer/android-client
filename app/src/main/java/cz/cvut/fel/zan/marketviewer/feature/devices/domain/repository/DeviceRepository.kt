package cz.cvut.fel.zan.marketviewer.feature.devices.domain.repository

import cz.cvut.fel.zan.marketviewer.core.domain.ApiResult
import cz.cvut.fel.zan.marketviewer.feature.devices.data.remote.dto.DeviceCreateResponseDto
import cz.cvut.fel.zan.marketviewer.feature.devices.data.remote.dto.DeviceNameAndHashDto
import cz.cvut.fel.zan.marketviewer.feature.devices.domain.model.MarketViewerDevice
import kotlinx.coroutines.flow.Flow

interface DeviceRepository {

    fun getDevicesAsFlow(): Flow<List<MarketViewerDevice>>

    suspend fun syncDevices(): ApiResult<Unit>
    suspend fun createDevice(deviceName: String): ApiResult<DeviceCreateResponseDto>
    suspend fun deleteDevice(deviceId: Int): ApiResult<Unit>
    suspend fun getDeviceNameAndHash(deviceId: Int): ApiResult<DeviceNameAndHashDto>
    suspend fun changeDeviceName(deviceId: Int, newName: String): ApiResult<Unit>
}