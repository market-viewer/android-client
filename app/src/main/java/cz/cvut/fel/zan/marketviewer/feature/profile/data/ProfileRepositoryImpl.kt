package cz.cvut.fel.zan.marketviewer.feature.profile.data

import android.util.Log
import cz.cvut.fel.zan.marketviewer.core.data.dto.ApiErrorDto
import cz.cvut.fel.zan.marketviewer.core.domain.ApiResult
import cz.cvut.fel.zan.marketviewer.core.network.safeApiCall
import cz.cvut.fel.zan.marketviewer.feature.devices.data.local.toEntity
import cz.cvut.fel.zan.marketviewer.feature.devices.data.remote.dto.DeviceDto
import cz.cvut.fel.zan.marketviewer.feature.devices.data.remote.dto.toDomain
import cz.cvut.fel.zan.marketviewer.feature.profile.data.remote.ApiKeyDeleteDto
import cz.cvut.fel.zan.marketviewer.feature.profile.data.remote.ApiKeyUploadDto
import cz.cvut.fel.zan.marketviewer.feature.profile.data.remote.UsernameAndApiKeyDto
import cz.cvut.fel.zan.marketviewer.feature.profile.data.remote.UsernameUpdateDto
import cz.cvut.fel.zan.marketviewer.feature.profile.domain.model.ApiKeyProvider
import cz.cvut.fel.zan.marketviewer.feature.profile.domain.repository.ProfileRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode

class ProfileRepositoryImpl(
    private val httpClient: HttpClient
) : ProfileRepository {
    override suspend fun getUsernameAndApiKeyInfo(): ApiResult<UsernameAndApiKeyDto> {
        return safeApiCall(onError = {errorMsg -> ApiResult.Error(errorMsg)}) {
            val response = httpClient.get("/user/profile")

            when (response.status) {
                HttpStatusCode.OK -> {
                    val dto = response.body<UsernameAndApiKeyDto>()

                    ApiResult.Success(dto)
                }
                HttpStatusCode.BadRequest -> {
                    val errorData = response.body<ApiErrorDto>()
                    ApiResult.Error(errorData.message)
                }
                else -> ApiResult.Error("Unexpected error")
            }
        }
    }

    override suspend fun updateUsername(newUsername: String): ApiResult<String> {
        return safeApiCall(onError = {errorMsg -> ApiResult.Error(errorMsg)}) {
            val response = httpClient.patch("/user/username") {
                setBody(UsernameUpdateDto(newUsername))
            }

            when (response.status) {
                HttpStatusCode.OK -> {
                    Log.d("username", response.toString())
                    val dto = response.body<UsernameUpdateDto>()

                    ApiResult.Success(dto.username)
                }
                HttpStatusCode.BadRequest -> {
                    val errorData = response.body<ApiErrorDto>()
                    ApiResult.Error(errorData.message)
                }
                else -> ApiResult.Error("Unexpected error")
            }
        }
    }

    override suspend fun updateApiKey(
        keyProvider: ApiKeyProvider,
        keyValue: String
    ): ApiResult<Unit> {
        return safeApiCall(onError = {errorMsg -> ApiResult.Error(errorMsg)}) {
            val response = httpClient.post("/user/apiKey") {
                setBody(ApiKeyUploadDto(keyProvider, keyValue))
            }

            when (response.status) {
                HttpStatusCode.Created -> {

                    ApiResult.Success(Unit)
                }
                HttpStatusCode.BadRequest -> {
                    val errorData = response.body<ApiErrorDto>()
                    ApiResult.Error(errorData.message)
                }
                else -> ApiResult.Error("Unexpected error")
            }
        }
    }

    override suspend fun deleteApiKey(keyProvider: ApiKeyProvider): ApiResult<Unit> {
        return safeApiCall(onError = {errorMsg -> ApiResult.Error(errorMsg)}) {
            val response = httpClient.delete("/user/apiKey") {
                setBody(ApiKeyDeleteDto(keyProvider))
            }

            when (response.status) {
                HttpStatusCode.OK -> {
                    ApiResult.Success(Unit)
                }
                HttpStatusCode.BadRequest -> {
                    val errorData = response.body<ApiErrorDto>()
                    ApiResult.Error(errorData.message)
                }
                else -> ApiResult.Error("Unexpected error")
            }
        }
    }

    override suspend fun deleteAccount(): ApiResult<Unit> {
        return safeApiCall(onError = {errorMsg -> ApiResult.Error(errorMsg)}) {
            val response = httpClient.delete("/user")

            when (response.status) {
                HttpStatusCode.OK -> {
                    ApiResult.Success(Unit)
                }
                HttpStatusCode.BadRequest -> {
                    val errorData = response.body<ApiErrorDto>()
                    ApiResult.Error(errorData.message)
                }
                else -> ApiResult.Error("Unexpected error")
            }
        }
    }

}