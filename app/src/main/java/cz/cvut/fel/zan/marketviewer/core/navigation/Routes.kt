package cz.cvut.fel.zan.marketviewer.core.navigation

import cz.cvut.fel.zan.marketviewer.feature.devices.domain.model.MarketViewerDevice
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable
    data object Login : Route
    @Serializable
    data object Register : Route

    @Serializable
    data object DeviceTabGraph : Route

    @Serializable
    data object DeviceList : Route

    @Serializable
    data class DeviceDetail(val deviceId: Int) : Route

    @Serializable
    data object Profile : Route
    @Serializable
    data object Settings : Route
    @Serializable
    data object MainApp : Route
}
