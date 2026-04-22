package cz.cvut.fel.zan.marketviewer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import cz.cvut.fel.zan.marketviewer.core.data.local.ThemeSettingsManager
import cz.cvut.fel.zan.marketviewer.core.data.local.ThemeState
import cz.cvut.fel.zan.marketviewer.core.navigation.AppNavHost
import cz.cvut.fel.zan.marketviewer.core.navigation.Route
import cz.cvut.fel.zan.marketviewer.core.presentation.theme.MarketViewerTheme
import cz.cvut.fel.zan.marketviewer.core.data.local.TokenManager
import org.koin.android.ext.android.inject
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {

    private val themeSettingsManager: ThemeSettingsManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val themeState by themeSettingsManager.themeFlow.collectAsStateWithLifecycle(
                initialValue = ThemeState()
            )

            val isDarkTheme = themeState.isDarkMode ?: isSystemInDarkTheme()

            MarketViewerTheme(
                darkTheme = isDarkTheme,
                dynamicColor = themeState.useDynamicColor
            ) {
                MarketViewerApp()
            }
        }
    }
}


@Composable
fun MarketViewerApp(
    navController: NavHostController = rememberNavController(),
    tokenManager: TokenManager = koinInject()
) {

    //listen to the global event stream
    LaunchedEffect(Unit) {
        tokenManager.loggedOutEvent.collect {
            navController.navigate(Route.Login) {
                popUpTo(0) {inclusive = true}
                launchSingleTop = true
            }
        }
    }

    AppNavHost(navController)
}