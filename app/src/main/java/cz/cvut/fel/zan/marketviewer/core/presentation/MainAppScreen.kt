package cz.cvut.fel.zan.marketviewer.core.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cz.cvut.fel.zan.marketviewer.core.navigation.Route
import cz.cvut.fel.zan.marketviewer.core.presentation.components.MarketViewerBottomBar
import cz.cvut.fel.zan.marketviewer.feature.devices.presentation.deviceGraph
import cz.cvut.fel.zan.marketviewer.feature.profile.presentation.ProfileScreen

@Composable
fun MainAppScreen(
    rootNavController: NavHostController
) {
    val bottomNavController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            MarketViewerBottomBar(navController = bottomNavController)
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = Route.DeviceTabGraph,
            modifier = Modifier.padding(innerPadding)
        ) {
            deviceGraph(bottomNavController)

            //add graphs for profile and
            composable<Route.Profile> {
                ProfileScreen()
            }

            composable<Route.Settings> {
                ProfileScreen()
            }
        }
    }
}
