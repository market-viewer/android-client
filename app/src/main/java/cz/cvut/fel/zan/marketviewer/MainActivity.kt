package cz.cvut.fel.zan.marketviewer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import cz.cvut.fel.zan.marketviewer.core.navigation.AppNavHost
import cz.cvut.fel.zan.marketviewer.core.navigation.Route
import cz.cvut.fel.zan.marketviewer.core.presentation.theme.MarketViewerTheme
import cz.cvut.fel.zan.marketviewer.core.utils.TokenManager
import kotlinx.coroutines.flow.compose
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MarketViewerTheme {
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

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}