package cz.cvut.fel.zan.marketviewer.feature.devices.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceDao {

    @Query("SELECT * FROM devices ORDER BY id DESC")
    fun getDevicesAsFlow(): Flow<List<DeviceEntity>>

    @Query("SELECT * FROM devices WHERE id = :deviceId")
    suspend fun getDeviceById(deviceId: Int): DeviceEntity?

    @Upsert
    suspend fun upsertDevice(device: DeviceEntity)

    @Upsert
    suspend fun upsertDevices(devices: List<DeviceEntity>)

    @Query("DELETE FROM devices WHERE id = :deviceId")
    suspend fun deleteDevice(deviceId: Int)

    @Query("DELETE FROM devices")
    suspend fun clearAllDevices()
}