package cz.cvut.fel.zan.marketviewer.feature.screens.domain.repository

import cz.cvut.fel.zan.marketviewer.core.domain.ApiResult
import cz.cvut.fel.zan.marketviewer.feature.screens.data.remote.dto.ScreenDto
import cz.cvut.fel.zan.marketviewer.feature.screens.domain.model.MarketViewerScreen

interface ScreenRepository {
    suspend fun getScreensForDevice(deviceId: Int) : ApiResult<List<MarketViewerScreen>>
    suspend fun deleteScreen(screenId: Int, deviceId: Int) : ApiResult<Unit>
    suspend fun reorderScreens(screensIds: List<Int>, deviceId: Int) : ApiResult<Unit>
}