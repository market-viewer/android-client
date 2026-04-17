package cz.cvut.fel.zan.marketviewer.feature.auth.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import cz.cvut.fel.zan.marketviewer.core.navigation.Route
import cz.cvut.fel.zan.marketviewer.core.utils.SSOCallbackEndpoint
import cz.cvut.fel.zan.marketviewer.feature.auth.presentation.login.LoginScreen
import cz.cvut.fel.zan.marketviewer.feature.auth.presentation.register.RegisterScreen

const val showSnackBarMsg = "show_registration_snackbar"

fun NavGraphBuilder.authGraph(navController: NavHostController) {
    composable<Route.Login>(
        //deep link, so that the callback from the sso, gets us back to the app
        deepLinks = listOf(
            navDeepLink {
                uriPattern = SSOCallbackEndpoint
            }
        )
    ) { backStackEntry ->
        val showSnackbar = backStackEntry.savedStateHandle.get<Boolean>(showSnackBarMsg) == true
        val ssoToken = backStackEntry.arguments?.getString("token")

        LoginScreen(
            ssoToken = ssoToken,
            onLoginSuccess = {
                navController.navigate(Route.MainApp) {
                    popUpTo(Route.Login) { inclusive = true }
                }
            },
            onRegisterClick = {
                navController.navigate(Route.Register) {
                    launchSingleTop = true
                }
            },
            showRegistrationSnackbar = showSnackbar,
            onSnackBarShown = { backStackEntry.savedStateHandle.remove<Boolean>(showSnackBarMsg)}
        )
    }
    composable<Route.Register> {
        RegisterScreen(
            onBackToLogin = {
                navController.popBackStack<Route.Login>(inclusive = false)
            },
            onRegistrationSuccessful = {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set(showSnackBarMsg, true)

                navController.popBackStack<Route.Login>(inclusive = false)
            }
        )
    }
}
