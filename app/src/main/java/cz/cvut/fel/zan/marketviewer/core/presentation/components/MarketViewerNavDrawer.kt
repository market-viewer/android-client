package cz.cvut.fel.zan.marketviewer.core.presentation.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import cz.cvut.fel.zan.marketviewer.R

@Composable
fun MarketViewerNavDrawer(
    navController: NavController,
    closeDrawer: () -> Unit,
    onLogout: () -> Unit
) {
    val itemShape = RoundedCornerShape(
        topStartPercent = 0,
        bottomStartPercent = 0,
        topEndPercent = 50,
        bottomEndPercent = 50,
    )

    ModalDrawerSheet {
        Spacer(Modifier.height(16.dp))

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        Text(
            text = "Market Viewer App",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.fillMaxWidth().padding(bottom = 30.dp),
            textAlign = TextAlign.Center,
        )

        // navigation items
        navDrawerItems.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true

            NavigationDrawerItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                    closeDrawer()
                },
                icon = { Icon(painter = painterResource(item.icon), contentDescription = item.name) },
                label = { Text(text = item.name) },
                modifier = Modifier.padding(start = 0.dp, end = 16.dp).padding(vertical = 5.dp),
                shape = itemShape
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        HorizontalDivider(modifier = Modifier.padding(horizontal = 28.dp, vertical = 8.dp))

        //logout button
        NavigationDrawerItem(
            selected = false,
            onClick = {
                closeDrawer()
                onLogout()
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.outline_logout_24),
                    contentDescription = "Logout",
                    tint = MaterialTheme.colorScheme.error
                )
            },
            label = {
                Text(
                    text = "Logout",
                    color = MaterialTheme.colorScheme.error
                )
            },
            modifier = Modifier
                .padding(start = 0.dp, end = 16.dp, bottom = 16.dp)
                .padding(vertical = 4.dp),
            shape = itemShape
        )
    }
}