package com.bina.navigation

import androidx.navigation.NavHostController

class NavigationManager(private val navController: NavHostController) {

    fun navigateToHome() {
        try {
            if (navController.currentDestination?.route != NavigationRoutes.HOME) {
                navController.navigate(NavigationRoutes.HOME) {
                    popUpTo(NavigationRoutes.HOME) { inclusive = true }
                    launchSingleTop = true
                }
            }
        } catch (e: Exception) {
            println("Navigation error: ${e.message}")
        }
    }

    fun canNavigateBack(): Boolean = navController.previousBackStackEntry != null

    fun navigateBack() {
        if (canNavigateBack()) {
            navController.popBackStack()
        }
    }

    fun getCurrentRoute(): String? = navController.currentDestination?.route
}

