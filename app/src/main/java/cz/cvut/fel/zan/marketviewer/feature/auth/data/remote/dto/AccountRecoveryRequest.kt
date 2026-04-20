package cz.cvut.fel.zan.marketviewer.feature.auth.data.remote.dto

import kotlinx.serialization.Serializable


@Serializable
data class AccountRecoveryRequest(
    val username: String,
    val recoverCode: String,
    val newPassword: String,
    val newPasswordRepeat: String
)
