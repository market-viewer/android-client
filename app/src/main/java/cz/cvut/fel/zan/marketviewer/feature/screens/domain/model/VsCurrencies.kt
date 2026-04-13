package cz.cvut.fel.zan.marketviewer.feature.screens.domain.model

enum class VsCurrencies(val label: String) {
    USD("US Dollar"),
    EUR("Euro"),
    CZK("Czech Crown"),
    GBP("British Pound"),
    JPY("Japanese Yen"),
    ETH("Ethereum"),
    BTC("Bitcoin"),
    SATS("Satoshis"),
    CNY("Chinese Yuan"),
    INR("Indian Rupee"),
    AUD("Australian Dollar"),
    RUB("Russian Ruble");

    companion object {
        fun fromString(displayLabel: String?): VsCurrencies {
            val currencyName = displayLabel?.substringBefore("(")
            return entries.find { it.name.equals(currencyName, ignoreCase = true) } ?: USD
        }
    }
}