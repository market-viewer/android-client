package cz.cvut.fel.zan.marketviewer.core.di

import androidx.room.Room
import cz.cvut.fel.zan.marketviewer.core.data.local.LocalDatabase
import cz.cvut.fel.zan.marketviewer.core.network.getHttpClient
import cz.cvut.fel.zan.marketviewer.core.data.local.ServerConfigManager
import cz.cvut.fel.zan.marketviewer.core.data.local.ThemeSettingsManager
import cz.cvut.fel.zan.marketviewer.core.data.local.TokenManager
import cz.cvut.fel.zan.marketviewer.core.data.local.UserProfileManager
import cz.cvut.fel.zan.marketviewer.feature.auth.data.AuthRepositoryImpl
import cz.cvut.fel.zan.marketviewer.feature.auth.domain.repository.AuthRepository
import cz.cvut.fel.zan.marketviewer.feature.auth.presentation.login.LoginViewModel
import cz.cvut.fel.zan.marketviewer.feature.auth.presentation.recovery.AccountRecoverViewModel
import cz.cvut.fel.zan.marketviewer.feature.auth.presentation.register.RegisterViewModel
import cz.cvut.fel.zan.marketviewer.feature.devices.data.DeviceRepositoryImpl
import cz.cvut.fel.zan.marketviewer.feature.devices.domain.repository.DeviceRepository
import cz.cvut.fel.zan.marketviewer.feature.devices.presentation.detail.DeviceDetailViewModel
import cz.cvut.fel.zan.marketviewer.feature.devices.presentation.list.DeviceListViewModel
import cz.cvut.fel.zan.marketviewer.feature.profile.data.ProfileRepositoryImpl
import cz.cvut.fel.zan.marketviewer.feature.profile.domain.repository.ProfileRepository
import cz.cvut.fel.zan.marketviewer.feature.profile.presentation.ProfileViewModel
import cz.cvut.fel.zan.marketviewer.feature.screens.data.ScreenRepositoryImpl
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.repository.ScreenRepository
import cz.cvut.fel.zan.marketviewer.feature.screens.presentation.edit.ScreenConfigViewModel
import cz.cvut.fel.zan.marketviewer.feature.settings.presentation.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

//for global singletons from core - add databse, netwrok, ...
val coreModule = module {
    //data stores
    single { TokenManager(context = androidContext(), database = get()) }
    single { UserProfileManager(context = androidContext()) }
    single { ServerConfigManager(context = androidContext()) }
    single { ThemeSettingsManager(context = androidContext()) }

    //local database
    single {
        Room.databaseBuilder(
            androidContext(),
            LocalDatabase::class.java,
            "local_db"
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    single { get<LocalDatabase>().screenDao }
    single { get<LocalDatabase>().deviceDao }
}

//for feature repositories, usecases, viewmodel,...
val featureModule = module {

    // AUTH
    single<AuthRepository> {
        AuthRepositoryImpl(httpClient = get())
    }

    //device
    single<DeviceRepository> {
        DeviceRepositoryImpl(httpClient = get(), deviceDao = get())
    }

    single<ScreenRepository> {
        ScreenRepositoryImpl(httpClient = get(), screenDao = get(), deviceDao = get())
    }


    //profile
    single<ProfileRepository> {
        ProfileRepositoryImpl(httpClient = get())
    }

    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
    viewModelOf(::DeviceListViewModel)
    viewModelOf(::DeviceDetailViewModel)
    viewModelOf(::ScreenConfigViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::AccountRecoverViewModel)
    viewModelOf(::SettingsViewModel)
}

// network functions
val networkModule = module {
    single {
        getHttpClient(tokenManager = get(), serverConfigManager = get())
    }
}

val appModules = listOf(coreModule, featureModule, networkModule)