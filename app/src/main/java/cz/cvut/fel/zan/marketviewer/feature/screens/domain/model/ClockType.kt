package cz.cvut.fel.zan.marketviewer.feature.screens.domain.model

enum class ClockType {
    ANALOG,
    DIGITAL;

    companion object {
        fun fromString(clockTypeName: String): ClockType {
            return ClockType.entries.find { it.name == clockTypeName } ?: ANALOG

        }
    }
}