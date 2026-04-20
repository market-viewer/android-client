package cz.cvut.fel.zan.marketviewer.feature.devices.data

import cz.cvut.fel.zan.marketviewer.core.data.dto.ApiErrorDto
import cz.cvut.fel.zan.marketviewer.core.domain.ApiResult
import cz.cvut.fel.zan.marketviewer.core.network.safeApiCall
import cz.cvut.fel.zan.marketviewer.feature.devices.data.local.DeviceDao
import cz.cvut.fel.zan.marketviewer.feature.devices.data.local.DeviceEntity
import cz.cvut.fel.zan.marketviewer.feature.devices.data.local.toDomain
import cz.cvut.fel.zan.marketviewer.feature.devices.data.local.toEntity
import cz.cvut.fel.zan.marketviewer.feature.devices.data.remote.dto.DeviceNameDto
import cz.cvut.fel.zan.marketviewer.feature.devices.data.remote.dto.DeviceCreateResponseDto
import cz.cvut.fel.zan.marketviewer.feature.devices.data.remote.dto.DeviceDto
import cz.cvut.fel.zan.marketviewer.feature.devices.data.remote.dto.DeviceNameAndHashDto
import cz.cvut.fel.zan.marketviewer.feature.devices.data.remote.dto.toDomain
import cz.cvut.fel.zan.marketviewer.feature.devices.domain.model.MarketViewerDevice
import cz.cvut.fel.zan.marketviewer.feature.devices.domain.repository.DeviceRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DeviceRepositoryImpl(
    private val httpClient: HttpClient,
    private val deviceDao: DeviceDao
) : DeviceRepository {

    override fun getDevicesAsFlow(): Flow<List<MarketViewerDevice>> {
        return deviceDao.getDevicesAsFlow().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun syncDevices(): ApiResult<Unit> {
        return safeApiCall(onError = {errorMsg -> ApiResult.Error(errorMsg)}) {
            val response = httpClient.get("device")

            when (response.status) {
                HttpStatusCode.OK -> {
                    val dtos = response.body<List<DeviceDto>>()
                    val entities = dtos.map { it.toDomain().toEntity() }

                    deviceDao.clearAllDevices()
                    deviceDao.upsertDevices(entities)

                    ApiResult.Success(Unit)
                }
                else -> ApiResult.Error("Unexpected error")
            }
        }
    }

    override suspend fun createDevice(deviceName: String): ApiResult<DeviceCreateResponseDto> {
        return safeApiCall(onError = {errorMsg -> ApiResult.Error(errorMsg)}) {
            val response = httpClient.post("device") {
                setBody(DeviceNameDto(deviceName))
            }


            when (response.status) {
                HttpStatusCode.Created -> {
                    val deviceData = response.body<DeviceCreateResponseDto>()

                    //add new device to local db
                    deviceDao.upsertDevice(DeviceEntity(id = deviceData.deviceId, name = deviceName, screenCount = 0))

                    ApiResult.Success(deviceData)
                }

                HttpStatusCode.BadRequest -> {
                    val errorData = response.body<ApiErrorDto>()
                    ApiResult.Error(errorData.message)
                }

                else -> ApiResult.Error("Unexpected error")
            }
        }
    }

    override suspend fun deleteDevice(deviceId: Int): ApiResult<Unit> {
        return safeApiCall(onError = {errorMsg -> ApiResult.Error(errorMsg)}) {
            val response = httpClient.delete("device/${deviceId}")


            when (response.status) {
                HttpStatusCode.NoContent -> {
                    deviceDao.deleteDevice(deviceId)

                    ApiResult.Success(Unit)
                }

                HttpStatusCode.NotFound -> {
                    ApiResult.Error("Device not found")
                }
                else -> ApiResult.Error("Unexpected error")
            }
        }
    }

    override suspend fun getDeviceNameAndHash(deviceId: Int): ApiResult<DeviceNameAndHashDto> {
        return safeApiCall(onError = {errorMsg -> ApiResult.Error(errorMsg)}) {
            val response = httpClient.get("device/${deviceId}")


            when (response.status) {
                HttpStatusCode.OK -> {
                    val deviceData = response.body<DeviceNameAndHashDto>()

                    //update local db
                    val existingDevice = deviceDao.getDeviceById(deviceId)
                    if (existingDevice != null) {
                        deviceDao.upsertDevice(existingDevice.copy(name = deviceData.name))
                    }

                    ApiResult.Success(deviceData)
                }

                HttpStatusCode.NotFound -> {
                    val errorData = response.body<ApiErrorDto>()
                    ApiResult.Error(errorData.message)
                }

                else -> ApiResult.Error("Unexpected error")
            }
        }
    }

    override suspend fun getDeviceNameLocal(deviceId: Int): String? {
        return deviceDao.getDeviceById(deviceId)?.name
    }

    override suspend fun changeDeviceName(deviceId: Int, newName: String): ApiResult<Unit> {
        return safeApiCall(onError = {errorMsg -> ApiResult.Error(errorMsg)}) {
            val response = httpClient.patch("device/${deviceId}/name") {
                setBody(DeviceNameDto(newName))
            }


            when (response.status) {
                HttpStatusCode.OK -> {

                    val existingDevice = deviceDao.getDeviceById(deviceId)
                    if (existingDevice != null) {
                        deviceDao.upsertDevice(existingDevice.copy(name = newName))
                    }

                    ApiResult.Success(Unit)
                }

                HttpStatusCode.NotFound, HttpStatusCode.BadRequest -> {
                    val errorData = response.body<ApiErrorDto>()
                    ApiResult.Error(errorData.message)
                }
                else -> ApiResult.Error("Unexpected error")
            }
        }
    }
}