package cz.cvut.fel.zan.marketviewer.feature.screens.domain.model

import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.StockTimeframe.DAY

enum class ClockTimeFormat(val label: String) {
    TWELVE_HOUR("12h"),
    TWENTY_FOUR_HOUR("24h");

    companion object {
        fun fromString(clockFormatLabel: String): ClockTimeFormat {
            return ClockTimeFormat.entries.find { it.label == clockFormatLabel } ?: TWENTY_FOUR_HOUR
        }
    }
}