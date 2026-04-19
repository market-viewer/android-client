package cz.cvut.fel.zan.marketviewer.core.presentation

import cz.cvut.fel.zan.marketviewer.core.presentation.components.MarketViewerNavDrawer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cz.cvut.fel.zan.marketviewer.core.navigation.Route
import cz.cvut.fel.zan.marketviewer.core.utils.TokenManager
import cz.cvut.fel.zan.marketviewer.core.utils.UserProfileManager
import cz.cvut.fel.zan.marketviewer.feature.devices.presentation.deviceGraph
import cz.cvut.fel.zan.marketviewer.feature.profile.presentation.ProfileScreen
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    rootNavController: NavHostController,
    userProfileManager: UserProfileManager = koinInject(),
    tokenManager: TokenManager = koinInject()
) {
    val innerNavController = rememberNavController()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    fun onLogout() {
        scope.launch {
            //clear the user data
            tokenManager.forceLogout()
            userProfileManager.clearProfile()
        }
    }


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MarketViewerNavDrawer (
                navController = innerNavController,
                closeDrawer = {
                    scope.launch { drawerState.close() }
                },
                onLogout = {
                    onLogout()
                }
            )
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            NavHost(
                navController = innerNavController,
                startDestination = Route.DeviceTabGraph,
                modifier = Modifier.padding(innerPadding)
            ) {
                deviceGraph(
                    innerNavController,
                    onDrawerOpen = { scope.launch { drawerState.open() } }
                )

                //add graphs for profile and settings
                composable<Route.Profile> {
                    ProfileScreen(
                        onLogout = { onLogout() },
                        onDrawerOpen = { scope.launch { drawerState.open() } }
                    )
                }

                composable<Route.Settings> {
                    ProfileScreen(
                        onDrawerOpen = {},
                        onLogout = {}
                    )
                }
            }
        }
    }
}
