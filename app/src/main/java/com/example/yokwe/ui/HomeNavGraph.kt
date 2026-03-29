package com.example.yokwe.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.yokwe.ui.goals.CreateGoalScreen
import com.example.yokwe.ui.goals.GoalsScreen
import com.example.yokwe.ui.home.HomeScreen

@Composable
fun HomeNavGraph(
    familyId: String,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                val items = listOf(
                    BottomNavItem.Home,
                    BottomNavItem.Goals
                )
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = modifier.padding(paddingValues)
        ) {
            composable(BottomNavItem.Home.route) {
                HomeScreen(familyId = familyId)
            }
            composable(BottomNavItem.Goals.route) {
                GoalsScreen(
                    onAddGoalClick = {
                        navController.navigate(MainRoutes.AddGoal.route)
                    }
                )
            }
            composable(MainRoutes.AddGoal.route) {
                CreateGoalScreen(
                    onGoalSaved = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomNavItem(MainRoutes.Home.route, "", Icons.Default.Home)
    object Goals : BottomNavItem(MainRoutes.Goals.route, "", Icons.Default.Checklist)
}

sealed class MainRoutes(val route: String) {
    object Home : MainRoutes("home")
    object Goals : MainRoutes("goals")
    object AddGoal : MainRoutes("add_goal")
}