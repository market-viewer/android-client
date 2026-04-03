package cz.cvut.fel.zan.marketviewer.feature.auth.data

import cz.cvut.fel.zan.marketviewer.core.network.safeApiCall
import cz.cvut.fel.zan.marketviewer.feature.auth.data.remote.dto.LoginRequestDto
import cz.cvut.fel.zan.marketviewer.feature.auth.data.remote.dto.LoginResponseDto
import cz.cvut.fel.zan.marketviewer.feature.auth.data.remote.dto.RegisterErrorResponseDto
import cz.cvut.fel.zan.marketviewer.feature.auth.data.remote.dto.RegisterRequestDto
import cz.cvut.fel.zan.marketviewer.feature.auth.data.remote.dto.RegisterResponseDto
import cz.cvut.fel.zan.marketviewer.feature.auth.domain.model.LoginResult
import cz.cvut.fel.zan.marketviewer.feature.auth.domain.model.RegisterResult
import cz.cvut.fel.zan.marketviewer.feature.auth.domain.repository.AuthRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.delay

class AuthRepositoryImpl(
    private val httpClient: HttpClient
) : AuthRepository {

    override suspend fun login( username: String, password: String): LoginResult {

        return safeApiCall(onError = { errorMessage -> LoginResult.Error(errorMessage) }) {

            val response = httpClient.post("auth/login") {
                setBody(LoginRequestDto(username, password))
            }

            when (response.status) {
                HttpStatusCode.OK -> {
                    val data = response.body<LoginResponseDto>()
                    LoginResult.Success(data.token)
                }

                HttpStatusCode.BadRequest -> LoginResult.Error("Invalid username or password")
                else -> LoginResult.Error("Unexpected error occurred")
            }
        }

    }

    override suspend fun register(
        username: String,
        password: String,
        passwordRepeat: String
    ): RegisterResult {

        return safeApiCall(onError = { errorMessage -> RegisterResult.Error(errorMessage) }) {

            val response = httpClient.post("auth/register") {
                setBody(RegisterRequestDto(username, password, passwordRepeat))
            }

            when (response.status) {
                HttpStatusCode.Created -> {
                    val data = response.body<RegisterResponseDto>()
                    RegisterResult.Success(data.recoveryCodes)
                }

                HttpStatusCode.BadRequest -> {
                    val data = response.body<RegisterErrorResponseDto>()
                    RegisterResult.Error(data.message)
                }
                else -> RegisterResult.Error("Unexpected error occurred")
            }
        }

    }

}