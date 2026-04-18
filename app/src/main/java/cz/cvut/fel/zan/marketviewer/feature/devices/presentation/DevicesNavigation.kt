package cz.cvut.fel.zan.marketviewer.feature.devices.presentation

import androidx.lifecycle.Lifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import cz.cvut.fel.zan.marketviewer.core.navigation.Route
import cz.cvut.fel.zan.marketviewer.feature.devices.presentation.detail.DeviceDetailScreen
import cz.cvut.fel.zan.marketviewer.feature.devices.presentation.list.DeviceListScreen

fun NavGraphBuilder.deviceGraph(
    navController: NavHostController,
    onDrawerOpen: () -> Unit
) {
    navigation<Route.DeviceTabGraph>(
        startDestination = Route.DeviceList
    ) {
        composable<Route.DeviceList> {
            DeviceListScreen(
                onNavigateToDeviceDetail = { deviceId ->
                    navController.navigate(Route.DeviceDetail(deviceId)) {
                        launchSingleTop = true
                    }
                },
                onDrawerOpen = onDrawerOpen
            )
        }

        composable<Route.DeviceDetail> {
            DeviceDetailScreen(
                onBackClicked = {
                    //doesnt brake when we spam the back button
                    if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED)  {
                        navController.popBackStack()
                    }
                },
                onDrawerOpen = onDrawerOpen
            )
        }
    }

}