package com.example.yokwe.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.yokwe.ui.auth.screens.AuthSelectionScreen
import com.example.yokwe.ui.auth.screens.CreateFamilyScreen
import com.example.yokwe.ui.auth.screens.JoinToFamilyScreen
import com.example.yokwe.ui.auth.screens.SignInScreen

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
                },
                onSignInClick = {
                    navController.navigate(AuthRoutes.SignIn.route)
                }
            )
        }

        composable(AuthRoutes.CreateFamily.route) {
            CreateFamilyScreen()
        }

        composable(AuthRoutes.JoinToFamily.route) {
            JoinToFamilyScreen()
        }

        composable(AuthRoutes.SignIn.route) {
            SignInScreen()
        }
    }
}

sealed class AuthRoutes(val route: String) {
    object SelectAuth : AuthRoutes("auth_selection")
    object CreateFamily : AuthRoutes("create_family")
    object JoinToFamily : AuthRoutes("join_family")
    object SignIn : AuthRoutes("sign_in")
}