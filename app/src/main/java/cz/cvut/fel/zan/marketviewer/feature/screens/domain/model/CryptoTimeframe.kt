package cz.cvut.fel.zan.marketviewer.feature.screens.domain.model

enum class CryptoTimeframe(val label: String) {
    HOUR("1h"),
    DAY("24h"),
    WEEK("7day"),
    TWO_WEEKS("14day"),
    MONTH("30day"),
    TWO_HUNDRED_DAYS("200day"),
    YEAR("1year");

    companion object {
        fun fromString(timeFrameLabel: String): CryptoTimeframe {
            return entries.find { it.label == timeFrameLabel } ?: DAY
        }
    }
}