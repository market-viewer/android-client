package cz.cvut.fel.zan.marketviewer.feature.devices.presentation

import androidx.lifecycle.Lifecycle
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
    composable<Route.DeviceList> { backStackEntry ->

        val deletedDeviceId = backStackEntry.savedStateHandle.get<Int>("deleted_device_id")

        DeviceListScreen(
            deletedDeviceId = deletedDeviceId,
            onDeletedDeviceHandled = {
                backStackEntry.savedStateHandle.remove<Int>("deleted_device_id")
            },
            onNavigateToDeviceDetail = { deviceId ->
                navController.navigate(Route.DeviceDetail(deviceId)) {
                    launchSingleTop = true
                }
            }
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
            //device was deleted on detail screen, pass the information to device list screen
            onDeviceDeleted = { deletedId ->
                navController.previousBackStackEntry?.savedStateHandle?.set("deleted_device_id", deletedId)

                navController.popBackStack()
            }
        )
    }

}