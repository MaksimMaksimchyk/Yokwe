package com.example.yokwe.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AuthNavGraph(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AuthRoutes.SelectAuth.route,
        modifier = modifier
    ) {
        composable(AuthRoutes.SelectAuth.route) {
            AuthSelectionScreen(
                onCreateFamilyClick = {
                    navController.navigate(AuthRoutes.CreateFamily.route)
                },
                onJoinFamilyClick = {
                    navController.navigate(AuthRoutes.JoinToFamily.route)
                }
            )
        }

        composable(AuthRoutes.CreateFamily.route) {
            CreateFamilyScreen()
        }

        composable(AuthRoutes.JoinToFamily.route) {
            JoinToFamilyScreen()
        }
    }
}

sealed class AuthRoutes(val route: String) {
    object SelectAuth : AuthRoutes("auth_selection")
    object CreateFamily : AuthRoutes("create_family")
    object JoinToFamily : AuthRoutes("join_family")
}