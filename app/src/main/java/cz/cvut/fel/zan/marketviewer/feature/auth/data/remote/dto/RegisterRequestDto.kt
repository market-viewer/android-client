package cz.cvut.fel.zan.marketviewer.feature.auth.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequestDto(
    val username: String,
    val password: String,
    val passwordRepeat: String
)
