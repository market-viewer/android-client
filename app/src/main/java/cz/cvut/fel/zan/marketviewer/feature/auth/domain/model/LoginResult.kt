package cz.cvut.fel.zan.marketviewer.feature.auth.domain.model

sealed class LoginResult {
    data class Success(val token: String) : LoginResult()
    data class Error(val msg: String) : LoginResult()
}