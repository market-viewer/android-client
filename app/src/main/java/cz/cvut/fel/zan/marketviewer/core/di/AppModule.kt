package cz.cvut.fel.zan.marketviewer.core.di

import android.util.Log
import cz.cvut.fel.zan.marketviewer.core.utils.TokenManager
import cz.cvut.fel.zan.marketviewer.core.utils.backendBaseUrl
import cz.cvut.fel.zan.marketviewer.feature.auth.data.AuthRepositoryImpl
import cz.cvut.fel.zan.marketviewer.feature.auth.domain.repository.AuthRepository
import cz.cvut.fel.zan.marketviewer.feature.auth.presentation.login.LoginViewModel
import cz.cvut.fel.zan.marketviewer.feature.auth.presentation.register.RegisterViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
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
    
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
}

// network functions
val networkModule = module {
    single {
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

        }
    }
}

val appModules = listOf(coreModule, featureModule, networkModule)