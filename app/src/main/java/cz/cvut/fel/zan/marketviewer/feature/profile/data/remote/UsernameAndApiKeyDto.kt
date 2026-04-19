package cz.cvut.fel.zan.marketviewer.feature.profile.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class UsernameAndApiKeyDto(
    val username: String,
    val apiKeyProviders: List<String>
)
