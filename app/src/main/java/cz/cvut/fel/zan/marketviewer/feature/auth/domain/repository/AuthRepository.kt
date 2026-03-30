package cz.cvut.fel.zan.marketviewer.feature.auth.domain.repository

import cz.cvut.fel.zan.marketviewer.feature.auth.domain.model.LoginResult
import cz.cvut.fel.zan.marketviewer.feature.auth.domain.model.RegisterResult

interface AuthRepository {
    suspend fun login(username: String, password: String): LoginResult

    suspend fun register(username: String, password: String, passwordRepeat: String): RegisterResult
}