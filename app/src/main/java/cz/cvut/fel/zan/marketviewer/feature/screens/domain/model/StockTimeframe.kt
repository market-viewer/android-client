package cz.cvut.fel.zan.marketviewer.feature.screens.domain.model

enum class StockTimeframe(val label: String) {
    FIVE_MINUTES("5min"),
    HOUR("1h"),
    FIVE_HOUR("5h"),
    DAY("24h"),
    WEEK("7day"),
    TWO_WEEKS("14day"),
    MONTH("30day"),
    YEAR("1year"),
    FIVE_YEARS("5year");

    companion object {
        fun fromString(timeFrameLabel: String): StockTimeframe {
            return entries.find { it.label == timeFrameLabel } ?: DAY
        }
    }
}