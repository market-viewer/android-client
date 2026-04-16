package cz.cvut.fel.zan.marketviewer.feature.devices.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "devices")
data class DeviceEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val screenCount: Int
)