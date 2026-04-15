package cz.cvut.fel.zan.marketviewer.feature.screens.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ScreenDao {

    @Query("SELECT * FROM screens WHERE deviceId = :deviceId ORDER BY position ASC")
    fun getScreensForDevice(deviceId: Int): Flow<List<ScreenEntity>>

    @Upsert
    suspend fun upsertScreen(screen: ScreenEntity)

    @Upsert
    suspend fun upsertScreens(screens: List<ScreenEntity>)

    @Query("DELETE FROM screens WHERE deviceId = :deviceId")
    suspend fun deleteScreensForDevice(deviceId: Int)

    @Query("DELETE FROM screens WHERE id = :screenId")
    suspend fun deleteScreen(screenId: Int)

}