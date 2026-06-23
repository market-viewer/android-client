package cz.cvut.fel.zan.marketviewer.core.network

import android.util.Log
import cz.cvut.fel.zan.marketviewer.core.data.local.ServerConfigManager
import cz.cvut.fel.zan.marketviewer.core.data.local.TokenManager
import cz.cvut.fel.zan.marketviewer.feature.auth.domain.model.LoginResult
import cz.cvut.fel.zan.marketviewer.feature.auth.domain.repository.AuthRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.reflect.instanceOf
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json

fun getHttpClient(tokenManager: TokenManager, serverConfigManager: ServerConfigManager, authRepositoryProvider: () -> AuthRepository): HttpClient {
    return HttpClient(CIO) {
        install(ContentNegotiation) { // define serialization
            json(Json {
                ignoreUnknownKeys = true //don't crash when server sends extra fields
                isLenient = true // relaxes strict JSON formating rules
                prettyPrint = true
            })
        }

        install(Logging) {
            // put ktor log to android logcat
            logger = object : Logger {
                override fun log(message: String) {
                    Log.d("Ktor network",message)
                }
            }
            level = LogLevel.BODY
        }


        defaultRequest {
            url.takeFrom(serverConfigManager.currentBaseUrl)
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }

        install(Auth) {
            bearer {
                loadTokens {
                    val token = tokenManager.tokenFlow.firstOrNull()
                    val refreshToken = tokenManager.tokenRefreshFlow.firstOrNull()
                    if (!token.isNullOrBlank() && !refreshToken.isNullOrBlank()) {
                        BearerTokens(token, refreshToken)
                    } else null
                }
                
                refreshTokens {
                    val requestPath = response.call.request.url.encodedPath
                    val isAuthEndpoint = requestPath.contains("auth/login") || 
                                         requestPath.contains("auth/register") || 
                                         requestPath.contains("auth/recover")
                    if (isAuthEndpoint) {
                        return@refreshTokens null
                    }

                    val refreshToken = tokenManager.tokenRefreshFlow.firstOrNull()
                    if (!refreshToken.isNullOrBlank()) {
                        val authRepository = authRepositoryProvider()
                        val refreshResponse = authRepository.refreshToken(refreshToken)
                        if (refreshResponse is LoginResult.Success) {
                            tokenManager.saveToken(refreshResponse.token, refreshResponse.refreshToken)
                            BearerTokens(refreshResponse.token, refreshResponse.refreshToken)
                        } else {
                            tokenManager.forceLogout()
                            null
                        }
                    } else {
                        tokenManager.forceLogout()
                        null
                    }
                }
                
                sendWithoutRequest { request ->
                    val requestPath = request.url.encodedPath
                    val isAuthEndpoint = requestPath.contains("auth/login") || 
                                         requestPath.contains("auth/refresh") || 
                                         requestPath.contains("auth/register") || 
                                         requestPath.contains("auth/recover")
                    !isAuthEndpoint // send token preemptively on non-auth endpoints
                }
            }
        }
    }
}