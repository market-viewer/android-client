package cz.cvut.fel.zan.marketviewer.core.di

import android.util.Log
import androidx.compose.ui.graphics.Paint
import cz.cvut.fel.zan.marketviewer.core.utils.TokenManager
import cz.cvut.fel.zan.marketviewer.core.utils.backendBaseUrl
import cz.cvut.fel.zan.marketviewer.feature.auth.data.AuthRepositoryImpl
import cz.cvut.fel.zan.marketviewer.feature.auth.domain.repository.AuthRepository
import cz.cvut.fel.zan.marketviewer.feature.auth.presentation.login.LoginViewModel
import cz.cvut.fel.zan.marketviewer.feature.auth.presentation.register.RegisterViewModel
import cz.cvut.fel.zan.marketviewer.feature.devices.data.DeviceRepositoryImpl
import cz.cvut.fel.zan.marketviewer.feature.devices.domain.repository.DeviceRepository
import cz.cvut.fel.zan.marketviewer.feature.devices.presentation.list.DeviceListViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.request.accept
import io.ktor.client.request.request
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

//for global singletons from core - add databse, netwrok, ...
val coreModule = module {
    single { TokenManager(androidContext()) }
}

//for feature repositories, usecases, viewmodel,...
val featureModule = module {

    // AUTH
    single<AuthRepository> {
        AuthRepositoryImpl(httpClient = get())
    }

    //device list
    single<DeviceRepository> {
        DeviceRepositoryImpl(httpClient = get())
    }
    
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
    viewModelOf(::DeviceListViewModel)
}

// network functions
val networkModule = module {
    single {
        val tokenManager = get<TokenManager>()

        HttpClient(Android) {
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
}

val appModules = listOf(coreModule, featureModule, networkModule)