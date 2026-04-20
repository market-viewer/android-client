package cz.cvut.fel.zan.marketviewer.feature.screens.domain.repository

import cz.cvut.fel.zan.marketviewer.core.domain.ApiResult
import cz.cvut.fel.zan.marketviewer.feature.screens.data.remote.dto.ScreenDto
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.MarketViewerScreen
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.ScreenType
import kotlinx.coroutines.flow.Flow

interface ScreenRepository {
    suspend fun getScreensForDevice(deviceId: Int) : Flow<List<MarketViewerScreen>>
    suspend fun syncScreens(deviceId: Int) : ApiResult<Unit>

    suspend fun deleteScreen(screenId: Int, deviceId: Int) : ApiResult<Unit>
    suspend fun deleteScreens(screenIds: Set<Int>, deviceId: Int) : ApiResult<Unit>
    suspend fun reorderScreens(screensIds: List<Int>, deviceId: Int) : ApiResult<Unit>
    suspend fun createScreen(deviceId: Int, screenType: ScreenType) : ApiResult<MarketViewerScreen>
    suspend fun updateScreen(deviceId: Int, updatedScreen: MarketViewerScreen) : ApiResult<MarketViewerScreen>
}