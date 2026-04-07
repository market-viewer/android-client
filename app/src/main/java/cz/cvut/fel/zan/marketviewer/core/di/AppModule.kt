package cz.cvut.fel.zan.marketviewer.core.di

import android.util.Log
import cz.cvut.fel.zan.marketviewer.core.network.getHttpClient
import cz.cvut.fel.zan.marketviewer.core.utils.TokenManager
import cz.cvut.fel.zan.marketviewer.core.utils.backendBaseUrl
import cz.cvut.fel.zan.marketviewer.feature.auth.data.AuthRepositoryImpl
import cz.cvut.fel.zan.marketviewer.feature.auth.domain.repository.AuthRepository
import cz.cvut.fel.zan.marketviewer.feature.auth.presentation.login.LoginViewModel
import cz.cvut.fel.zan.marketviewer.feature.auth.presentation.register.RegisterViewModel
import cz.cvut.fel.zan.marketviewer.feature.devices.data.DeviceRepositoryImpl
import cz.cvut.fel.zan.marketviewer.feature.devices.domain.repository.DeviceRepository
import cz.cvut.fel.zan.marketviewer.feature.devices.presentation.detail.DeviceDetailViewModel
import cz.cvut.fel.zan.marketviewer.feature.devices.presentation.list.DeviceListViewModel
import cz.cvut.fel.zan.marketviewer.feature.screens.data.remote.ScreenRepositoryImpl
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.repository.ScreenRepository
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

    //device
    single<DeviceRepository> {
        DeviceRepositoryImpl(httpClient = get())
    }

    single<ScreenRepository> {
        ScreenRepositoryImpl(httpClient = get())
    }

    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
    viewModelOf(::DeviceListViewModel)
    viewModelOf(::DeviceDetailViewModel)
}

// network functions
val networkModule = module {
    single {
        getHttpClient(tokenManager = get())
    }
}

val appModules = listOf(coreModule, featureModule, networkModule)