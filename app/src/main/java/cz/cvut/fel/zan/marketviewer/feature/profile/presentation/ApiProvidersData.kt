package cz.cvut.fel.zan.marketviewer.feature.profile.presentation

import cz.cvut.fel.zan.marketviewer.R
import cz.cvut.fel.zan.marketviewer.feature.profile.domain.model.ApiKeyProvider

data class ApiProviderDisplayData(
    val apiKeyProvider: ApiKeyProvider,
    val providerLogo: Int,
    val providerUrl: String
)

val apiProviderData = listOf(
    ApiProviderDisplayData(
        apiKeyProvider = ApiKeyProvider.COINGECKO,
        providerLogo = R.drawable.coingecko_icon,
        providerUrl = "https://www.coingecko.com/en/api/pricing"
    ),
    ApiProviderDisplayData(
        apiKeyProvider = ApiKeyProvider.TWELVE_DATA,
        providerLogo = R.drawable.twelvedata_icon,
        providerUrl = "https://twelvedata.com/login"
    ),
    ApiProviderDisplayData(
        apiKeyProvider = ApiKeyProvider.GEMINI,
        providerLogo = R.drawable.google_gemini_icon,
        providerUrl = "https://aistudio.google.com/api-keys"
    )
)