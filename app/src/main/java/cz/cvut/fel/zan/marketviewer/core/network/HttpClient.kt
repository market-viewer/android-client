package cz.cvut.fel.zan.marketviewer.core.network

import android.util.Log
import cz.cvut.fel.zan.marketviewer.core.utils.TokenManager
import cz.cvut.fel.zan.marketviewer.core.utils.backendBaseUrl
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json

fun getHttpClient(tokenManager: TokenManager): HttpClient {
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
            url(backendBaseUrl)
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }

        //logout user when the saved token is wrong
        HttpResponseValidator {
            validateResponse { response ->
                if (response.status == HttpStatusCode.Unauthorized) {
                    tokenManager.forceLogout()
                }
            }
        }
    }.apply {
        //add jwt token automatically to each request
        requestPipeline.intercept(HttpRequestPipeline.State) {
            val currentToken = tokenManager.tokenFlow.firstOrNull()
            if (!currentToken.isNullOrBlank()) {
                context.headers.append(HttpHeaders.Authorization, "Bearer $currentToken")
            }
        }
    }
}