package cz.cvut.fel.zan.marketviewer.core.presentation.components

import cz.cvut.fel.zan.marketviewer.R
import cz.cvut.fel.zan.marketviewer.core.navigation.Route

data class BottomNavItem(
    val name: String,
    val icon: Int,
    val route: Route
)

val bottomNavItems = listOf(
    BottomNavItem("Devices", R.drawable.outline_devices_24, Route.DeviceTabGraph),
    BottomNavItem("Profile", R.drawable.face_5_24px, Route.Profile),
    BottomNavItem("Settings", R.drawable.settings_24px, Route.Settings)
)