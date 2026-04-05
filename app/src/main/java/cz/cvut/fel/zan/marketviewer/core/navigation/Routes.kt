package cz.cvut.fel.zan.marketviewer.core.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable
    data object Login : Route
    @Serializable
    data object Register : Route

    @Serializable
    data object DeviceList : Route

    @Serializable
    data class DeviceDetail(val deviceId: Int) : Route

    @Serializable
    data object DeviceCreate : Route
}
