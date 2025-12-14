package com.bina.navigation

import androidx.navigation.NavHostController

class NavigationManager(
    private val navController: NavHostController,
    private val onError: (Throwable) -> Unit = {}
) {
    fun navigateToHome(): Boolean {
        if (navController.currentDestination?.route == NavigationRoutes.HOME) return false

        return runCatching {
            navController.navigate(NavigationRoutes.HOME) {
                launchSingleTop = true
            }
        }.onFailure(onError).isSuccess
    }

    fun navigateBack(): Boolean = navController.popBackStack()
    fun getCurrentRoute(): String? = navController.currentDestination?.route
}


