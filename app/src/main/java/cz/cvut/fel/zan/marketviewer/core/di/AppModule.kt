package cz.cvut.fel.zan.marketviewer.core.di

import cz.cvut.fel.zan.marketviewer.feature.auth.data.AuthRepositoryImpl
import cz.cvut.fel.zan.marketviewer.feature.auth.domain.repository.AuthRepository
import cz.cvut.fel.zan.marketviewer.feature.auth.presentation.login.LoginViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel

//for global singletons from core - add databse, netwrok, ...
val coreModule = module {

}

//for feature repositories, usecases, viewmodel,...
val featureModule = module {

    // AUTH
    single<AuthRepository> {
        AuthRepositoryImpl()
    }
    viewModel {
        LoginViewModel(get())
    }

}

val appModules = listOf(coreModule, featureModule)