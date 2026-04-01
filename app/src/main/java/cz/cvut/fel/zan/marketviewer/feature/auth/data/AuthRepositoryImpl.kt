package cz.cvut.fel.zan.marketviewer.feature.auth.data

import cz.cvut.fel.zan.marketviewer.feature.auth.domain.model.LoginResult
import cz.cvut.fel.zan.marketviewer.feature.auth.domain.model.RegisterResult
import cz.cvut.fel.zan.marketviewer.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.delay

class AuthRepositoryImpl : AuthRepository {
    override suspend fun login(
        username: String,
        password: String
    ): LoginResult {

        delay(2000)

        return if (Math.random() > 0.5) {
            LoginResult.Success(token = "123456789")
        } else {
            LoginResult.Error("Invalid username or password")
        }

    }

    override suspend fun register(
        username: String,
        password: String,
        passwordRepeat: String
    ): RegisterResult {

        delay(2000)

        return if (Math.random() > 0.2) {
            RegisterResult.Success(listOf("ALPHA-1234-ABCD", "ALPHA-1234-sdf5", "ALPHA-1234-ABCDsd", "ALPHA-1234-ABCD", "ALPHA-1234-sdf5", "ALPHA-1234-ABCDsd"))
        } else {
            RegisterResult.Error("Username already taken")
        }

    }

}