package cz.cvut.fel.zan.marketviewer.feature.auth.data.remote.dto

import kotlinx.serialization.Serializable


@Serializable
data class LoginResponseDto(
    val token: String,
    val refreshToken: String
)
