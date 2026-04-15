package cz.cvut.fel.zan.marketviewer.feature.screens.domain.model

enum class GraphType(val label: String) {
    LINE("Line"),
    CANDLE("Candle");

    companion object {
        fun fromString(graphTypeLabel: String): GraphType {
            return entries.find { it.label == graphTypeLabel} ?: LINE
        }
    }

}