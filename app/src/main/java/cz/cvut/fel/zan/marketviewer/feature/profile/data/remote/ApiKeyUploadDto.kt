package cz.cvut.fel.zan.marketviewer.feature.profile.data.remote

import cz.cvut.fel.zan.marketviewer.feature.profile.domain.model.ApiKeyProvider
import kotlinx.serialization.Serializable

@Serializable
data class ApiKeyUploadDto (
    val endpoint: ApiKeyProvider,
    val keyValue: String
)