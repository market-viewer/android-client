package cz.cvut.fel.zan.marketviewer.feature.auth.domain.model

sealed class RegisterResult {
    data class Success(val recoveryCodes: List<String>) : RegisterResult()
    data class Error(val msg: String) : RegisterResult()
}