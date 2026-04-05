package cz.cvut.fel.zan.marketviewer.feature.devices.data

import cz.cvut.fel.zan.marketviewer.core.data.dto.ApiErrorDto
import cz.cvut.fel.zan.marketviewer.core.domain.ApiResult
import cz.cvut.fel.zan.marketviewer.core.network.safeApiCall
import cz.cvut.fel.zan.marketviewer.feature.devices.data.remote.dto.DeviceCreateRequestDto
import cz.cvut.fel.zan.marketviewer.feature.devices.data.remote.dto.DeviceCreateResponseDto
import cz.cvut.fel.zan.marketviewer.feature.devices.data.remote.dto.DeviceDto
import cz.cvut.fel.zan.marketviewer.feature.devices.data.remote.dto.toDomain
import cz.cvut.fel.zan.marketviewer.feature.devices.domain.model.MarketViewerDevice
import cz.cvut.fel.zan.marketviewer.feature.devices.domain.repository.DeviceRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode

class DeviceRepositoryImpl(
    private val httpClient: HttpClient
) : DeviceRepository {

    override suspend fun listDevices(): ApiResult<List<MarketViewerDevice>> {
        return safeApiCall(onError = {errorMsg -> ApiResult.Error(errorMsg)}) {
            val response = httpClient.get("device")

            when (response.status) {
                HttpStatusCode.OK -> {
                    val dtos = response.body<List<DeviceDto>>()

                    val domainModels = dtos.map { it.toDomain() }
                    ApiResult.Success(domainModels)
                }
                else -> ApiResult.Error("Unexpected error")
            }
        }
    }

    override suspend fun createDevice(deviceName: String): ApiResult<DeviceCreateResponseDto> {
        return safeApiCall(onError = {errorMsg -> ApiResult.Error(errorMsg)}) {
            val response = httpClient.post("device") {
                setBody(DeviceCreateRequestDto(deviceName))
            }


            when (response.status) {
                HttpStatusCode.Created -> {
                    val deviceData = response.body<DeviceCreateResponseDto>()
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
                    ApiResult.Success(Unit)
                }

                HttpStatusCode.NotFound -> {
                    ApiResult.Error("Device not found")
                }
                else -> ApiResult.Error("Unexpected error")
            }
        }
    }
}