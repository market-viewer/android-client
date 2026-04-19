package cz.cvut.fel.zan.marketviewer.feature.profile.domain.repository

import cz.cvut.fel.zan.marketviewer.core.domain.ApiResult
import cz.cvut.fel.zan.marketviewer.feature.profile.data.remote.UsernameAndApiKeyDto
import cz.cvut.fel.zan.marketviewer.feature.profile.domain.model.ApiKeyProvider

interface ProfileRepository {

    suspend fun getUsernameAndApiKeyInfo(): ApiResult<UsernameAndApiKeyDto>

    suspend fun updateUsername(newUsername: String): ApiResult<String>

    suspend fun updateApiKey(keyProvider: ApiKeyProvider, keyValue: String) : ApiResult<Unit>

    suspend fun deleteApiKey(keyProvider: ApiKeyProvider) : ApiResult<Unit>

    suspend fun deleteAccount() : ApiResult<Unit>
}
