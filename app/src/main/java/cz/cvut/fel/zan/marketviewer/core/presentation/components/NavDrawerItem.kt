package cz.cvut.fel.zan.marketviewer.core.presentation.components

import cz.cvut.fel.zan.marketviewer.R
import cz.cvut.fel.zan.marketviewer.core.navigation.Route

data class NavDrawerItem(
    val name: String,
    val icon: Int,
    val route: Route
)

val navDrawerItems = listOf(
    NavDrawerItem("Devices", R.drawable.outline_devices_24, Route.DeviceTabGraph),
    NavDrawerItem("Profile", R.drawable.face_5_24px, Route.Profile),
    NavDrawerItem("Settings", R.drawable.settings_24px, Route.Settings)
)