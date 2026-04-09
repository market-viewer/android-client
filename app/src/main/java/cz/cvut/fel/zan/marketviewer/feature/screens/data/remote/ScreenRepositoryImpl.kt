package cz.cvut.fel.zan.marketviewer.feature.screens.data.remote

import cz.cvut.fel.zan.marketviewer.core.domain.ApiResult
import cz.cvut.fel.zan.marketviewer.core.network.safeApiCall
import cz.cvut.fel.zan.marketviewer.feature.devices.data.remote.dto.DeviceDto
import cz.cvut.fel.zan.marketviewer.feature.devices.data.remote.dto.toDomain
import cz.cvut.fel.zan.marketviewer.feature.devices.domain.model.MarketViewerDevice
import cz.cvut.fel.zan.marketviewer.feature.screens.data.remote.dto.ScreenDto
import cz.cvut.fel.zan.marketviewer.feature.screens.data.remote.dto.toDomain
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.MarketViewerScreen
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.repository.ScreenRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode

class ScreenRepositoryImpl(
    private val httpClient: HttpClient
) : ScreenRepository {

    override suspend fun getScreensForDevice(deviceId: Int): ApiResult<List<MarketViewerScreen>> {
        return safeApiCall(onError = {errorMsg -> ApiResult.Error(errorMsg)}) {
            val response = httpClient.get("device/$deviceId/screen")

            when (response.status) {
                HttpStatusCode.OK -> {
                    val dtos = response.body<List<ScreenDto>>()

                    val domainModels = dtos.map { it.toDomain() }
                    ApiResult.Success(domainModels)
                }

                HttpStatusCode.NotFound -> {
                    ApiResult.Error("Device not found")
                }

                else -> ApiResult.Error("Unexpected error")
            }
        }
    }

    override suspend fun deleteScreen(screenId: Int, deviceId: Int): ApiResult<Unit> {
        return safeApiCall(onError = {errorMsg -> ApiResult.Error(errorMsg)}) {
            val response = httpClient.delete("device/$deviceId/screen/$screenId")

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