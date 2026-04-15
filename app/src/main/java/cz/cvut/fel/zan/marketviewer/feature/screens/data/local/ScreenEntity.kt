package cz.cvut.fel.zan.marketviewer.feature.screens.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "screens")
data class ScreenEntity(
    // base properties
    @PrimaryKey val id: Int,
    val deviceId: Int,
    val position: Int,
    val screenType: String,

    // Shared / Config properties
    val timeFrame: String? = null,
    val graphType: String? = null,
    val displayGraph: Boolean? = null,
    val simpleDisplay: Boolean? = null,
    val fetchInterval: Int? = null,

    // Crypto & Stock specific
    val assetName: String? = null, // Ticker/Symbol
    val currency: String? = null,

    // Clock specific
    val timezone: String? = null,
    val timezoneCode: String? = null,
    val clockType: String? = null,
    val timeFormat: String? = null,

    // Timer specific
    val timerName: String? = null,

    // AI Text specific
    val prompt: String? = null,
    val fetchIntervalHours: Int? = null
)