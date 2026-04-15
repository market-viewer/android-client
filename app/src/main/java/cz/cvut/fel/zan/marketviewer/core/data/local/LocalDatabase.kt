package cz.cvut.fel.zan.marketviewer.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import cz.cvut.fel.zan.marketviewer.feature.screens.data.local.ScreenDao
import cz.cvut.fel.zan.marketviewer.feature.screens.data.local.ScreenEntity

@Database(
    entities = [
        ScreenEntity::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class LocalDatabase : RoomDatabase() {
    abstract val screenDao: ScreenDao
}