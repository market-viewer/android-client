package cz.cvut.fel.zan.marketviewer.feature.devices.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import cz.cvut.fel.zan.marketviewer.core.navigation.Route
import cz.cvut.fel.zan.marketviewer.feature.auth.presentation.login.LoginScreen
import cz.cvut.fel.zan.marketviewer.feature.auth.presentation.register.RegisterScreen
import cz.cvut.fel.zan.marketviewer.feature.devices.presentation.detail.DeviceDetailScreen
import cz.cvut.fel.zan.marketviewer.feature.devices.presentation.list.DeviceListScreen

fun NavGraphBuilder.deviceGraph(navController: NavHostController) {
    composable<Route.DeviceList> {
        DeviceListScreen(
            onNavigateToDeviceDetail = { deviceId ->
                navController.navigate(Route.DeviceDetail(deviceId)) {
                    launchSingleTop = true
                }
            }
        )
    }

    composable<Route.DeviceDetail> { backStackEntry ->
        DeviceDetailScreen(
            onBackClicked = { navController.popBackStack() }
        )
    }

}