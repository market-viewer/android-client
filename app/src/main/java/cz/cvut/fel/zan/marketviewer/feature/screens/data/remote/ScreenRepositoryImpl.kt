package cz.cvut.fel.zan.marketviewer.feature.screens.data.remote

import cz.cvut.fel.zan.marketviewer.core.data.dto.ApiErrorDto
import cz.cvut.fel.zan.marketviewer.core.domain.ApiResult
import cz.cvut.fel.zan.marketviewer.core.network.safeApiCall
import cz.cvut.fel.zan.marketviewer.feature.devices.data.remote.dto.DeviceDto
import cz.cvut.fel.zan.marketviewer.feature.devices.data.remote.dto.toDomain
import cz.cvut.fel.zan.marketviewer.feature.devices.domain.model.MarketViewerDevice
import cz.cvut.fel.zan.marketviewer.feature.screens.data.local.ScreenDao
import cz.cvut.fel.zan.marketviewer.feature.screens.data.local.toDomain
import cz.cvut.fel.zan.marketviewer.feature.screens.data.local.toEntity
import cz.cvut.fel.zan.marketviewer.feature.screens.data.remote.dto.ReorderScreensRequest
import cz.cvut.fel.zan.marketviewer.feature.screens.data.remote.dto.ScreenCreateDto
import cz.cvut.fel.zan.marketviewer.feature.screens.data.remote.dto.ScreenDto
import cz.cvut.fel.zan.marketviewer.feature.screens.data.remote.dto.toDomain
import cz.cvut.fel.zan.marketviewer.feature.screens.data.remote.dto.toDto
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.MarketViewerScreen
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.ScreenType
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.repository.ScreenRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ScreenRepositoryImpl(
    private val httpClient: HttpClient,
    private val screenDao: ScreenDao
) : ScreenRepository {

    override suspend fun getScreensForDevice(deviceId: Int): Flow<List<MarketViewerScreen>> {
        return screenDao.getScreensForDevice(deviceId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun syncScreens(deviceId: Int): ApiResult<Unit> {
        return safeApiCall(onError = {errorMsg -> ApiResult.Error(errorMsg)}) {
            val response = httpClient.get("device/$deviceId/screen")

            when (response.status) {
                HttpStatusCode.OK -> {
                    val dtos = response.body<List<ScreenDto>>()

                    val entities = dtos.map { it.toDomain().toEntity(deviceId) }

                    screenDao.deleteScreensForDevice(deviceId)
                    screenDao.upsertScreens(entities)

                    ApiResult.Success(Unit)
                }

                HttpStatusCode.NotFound -> { ApiResult.Error("Device not found") }
                else -> ApiResult.Error("Unexpected error")
            }
        }
    }

    override suspend fun deleteScreen(screenId: Int, deviceId: Int): ApiResult<Unit> {
        return safeApiCall(onError = {errorMsg -> ApiResult.Error(errorMsg)}) {
            val response = httpClient.delete("device/$deviceId/screen/$screenId")

            when (response.status) {
                HttpStatusCode.NoContent -> {
                    screenDao.deleteScreen(screenId)

                    ApiResult.Success(Unit)
                }

                HttpStatusCode.NotFound -> {
                    ApiResult.Error("Device not found")
                }

                else -> ApiResult.Error("Unexpected error")
            }
        }
    }

    override suspend fun reorderScreens(screensIds: List<Int>, deviceId: Int): ApiResult<Unit> {
        return safeApiCall(onError = {errorMsg -> ApiResult.Error(errorMsg)}) {
            val response = httpClient.patch("device/$deviceId/screen/order") {
                setBody(ReorderScreensRequest(screensIds))
            }

            when (response.status) {
                HttpStatusCode.OK -> {
                    syncScreens(deviceId)
                    ApiResult.Success(Unit)
                }

                HttpStatusCode.NotFound -> {
                    ApiResult.Error("Device not found")
                }

                HttpStatusCode.BadRequest -> {
                    val errorData = response.body<ApiErrorDto>()
                    ApiResult.Error(errorData.message)
                }

                else -> ApiResult.Error("Unexpected error")
            }
        }
    }

    override suspend fun createScreen(deviceId: Int, screenType: ScreenType): ApiResult<MarketViewerScreen> {
        return safeApiCall(onError = {errorMsg -> ApiResult.Error(errorMsg)}) {
            val response = httpClient.post("device/$deviceId/screen") {
                setBody(ScreenCreateDto(screenType.name))
            }

            when (response.status) {
                HttpStatusCode.Created -> {
                    val newScreenDto = response.body<ScreenDto>()
                    val domainModel = newScreenDto.toDomain()

                    screenDao.upsertScreen(domainModel.toEntity(deviceId))

                    ApiResult.Success(domainModel)
                }

                HttpStatusCode.BadRequest, HttpStatusCode.NotFound -> {
                    val errorData = response.body<ApiErrorDto>()
                    ApiResult.Error(errorData.message)
                }

                else -> ApiResult.Error("Unexpected error")
            }
        }
    }

    override suspend fun updateScreen(deviceId: Int, updatedScreen: MarketViewerScreen) : ApiResult<MarketViewerScreen> {
        return safeApiCall(onError = {errorMsg -> ApiResult.Error(errorMsg)}) {
            val updatedScreenDto = updatedScreen.toDto()
            val response = httpClient.put("device/$deviceId/screen/${updatedScreenDto.id}") {
                setBody(updatedScreenDto)
            }

            when (response.status) {
                HttpStatusCode.OK -> {
                    val updatedScreen = response.body<ScreenDto>()
                    val domainModel = updatedScreen.toDomain()

                    screenDao.upsertScreen(domainModel.toEntity(deviceId))

                    ApiResult.Success(domainModel)
                }

                HttpStatusCode.BadRequest, HttpStatusCode.NotFound -> {
                    val errorData = response.body<ApiErrorDto>()
                    ApiResult.Error(errorData.message)
                }

                else -> ApiResult.Error("Unexpected error")
            }
        }
    }


}