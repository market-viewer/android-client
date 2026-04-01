package cz.cvut.fel.zan.marketviewer.core.di

import cz.cvut.fel.zan.marketviewer.feature.auth.data.AuthRepositoryImpl
import cz.cvut.fel.zan.marketviewer.feature.auth.domain.repository.AuthRepository
import cz.cvut.fel.zan.marketviewer.feature.auth.presentation.login.LoginViewModel
import cz.cvut.fel.zan.marketviewer.feature.auth.presentation.register.RegisterViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

//for global singletons from core - add databse, netwrok, ...
val coreModule = module {

}

//for feature repositories, usecases, viewmodel,...
val featureModule = module {

    // AUTH
    single<AuthRepository> {
        AuthRepositoryImpl()
    }
    
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)

}

val appModules = listOf(coreModule, featureModule)