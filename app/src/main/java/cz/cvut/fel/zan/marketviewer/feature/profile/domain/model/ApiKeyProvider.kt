package cz.cvut.fel.zan.marketviewer.feature.profile.domain.model

enum class ApiKeyProvider(val displayName: String) {
    COINGECKO("Coingecko (crypto)"),
    TWELVE_DATA("TwelveData (stock)"),
    GEMINI("Gemini (AI)")
}