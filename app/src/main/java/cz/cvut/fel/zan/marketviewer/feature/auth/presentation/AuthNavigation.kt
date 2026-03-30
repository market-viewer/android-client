package cz.cvut.fel.zan.marketviewer.feature.auth.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import cz.cvut.fel.zan.marketviewer.core.navigation.Route
import cz.cvut.fel.zan.marketviewer.feature.auth.presentation.login.LoginScreen
import cz.cvut.fel.zan.marketviewer.feature.auth.presentation.register.RegisterScreen

fun NavGraphBuilder.authGraph(navController: NavHostController) {
    composable<Route.Login> {
        LoginScreen(
            onLoginSuccess = {
                navController.navigate(Route.DeviceList) {
                    popUpTo(Route.Login) { inclusive = true }
                }
            },
            onRegisterClick = {
                navController.navigate(Route.Register) {
                    launchSingleTop = true
                }

            }
        )
    }
    composable<Route.Register> {
        RegisterScreen(
            onRegisterSuccess = {
                navController.navigate(Route.Login) {
                    popUpTo(Route.Login) { inclusive = true }
                }
            },
            onBackToLogin = {
                navController.popBackStack()
            }
        )
    }
}
