package com.mobiledeveloper.vktube.navigation

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mobiledeveloper.vktube.ui.theme.Fronton

@Composable
fun MainBottomNavigation(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navController.currentDestination?.route
    if (shouldShowBottomBar(navBackStackEntry)) {
        BottomNavigation(
           backgroundColor = Fronton.color.backgroundSecondary
        ) {
            items.forEach { item ->
                BottomNavigationItem(
                    selected = currentRoute?.startsWith(item.route) == true,
                    onClick = {
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                launchSingleTop = true
                                popUpTo(navController.graph.startDestinationId)
                            }
                        }
                    },
                    icon = {
                        Icon(
                                imageVector = item.icon,
                                contentDescription = item.route
                        )
                    },
                )
            }
        }
    }
}


private fun shouldShowBottomBar(backStackEntry: NavBackStackEntry?): Boolean {
    return backStackEntry?.destination?.route in items.map { it.route }
}

val items = listOf(
    BottomNavigationItem(
        route = NavigationTree.Root.Main.name,
        icon = Icons.Default.List
    ),
    BottomNavigationItem(
        route = NavigationTree.Root.Settings.name,
        icon = Icons.Default.Settings
    ),
)