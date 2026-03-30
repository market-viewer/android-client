package cz.cvut.fel.zan.marketviewer.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import cz.cvut.fel.zan.marketviewer.feature.auth.presentation.authGraph
import cz.cvut.fel.zan.marketviewer.feature.devices.presentation.deviceGraph

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Route.Login,
        modifier = modifier
    ) {
        authGraph(navController)
        deviceGraph(navController)
    }
}

