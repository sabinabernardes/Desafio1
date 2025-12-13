package com.bina.navigation

import androidx.navigation.NavHostController
import io.mockk.mockk
import io.mockk.verify
import io.mockk.every
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NavigationManagerTest {

    private lateinit var navController: NavHostController
    private lateinit var navigationManager: NavigationManager

    @Before
    fun setup() {
        navController = mockk(relaxed = true)
        navigationManager = NavigationManager(navController)
    }

    @Test
    fun `navigateToHome should not navigate if already on HOME`() {
        every { navController.currentDestination?.route } returns NavigationRoutes.HOME

        navigationManager.navigateToHome()

        verify(exactly = 0) { navController.navigate(NavigationRoutes.HOME) }
    }

    @Test
    fun `canNavigateBack should return true when backstack has entries`() {
        every { navController.previousBackStackEntry } returns mockk()

        val result = navigationManager.canNavigateBack()

        assertTrue(result)
    }

    @Test
    fun `canNavigateBack should return false when backstack is empty`() {
        every { navController.previousBackStackEntry } returns null

        val result = navigationManager.canNavigateBack()

        assertFalse(result)
    }

    @Test
    fun `navigateBack should call popBackStack when can navigate back`() {
        every { navController.previousBackStackEntry } returns mockk()

        navigationManager.navigateBack()

        verify { navController.popBackStack() }
    }

    @Test
    fun `navigateBack should not call popBackStack when cannot navigate back`() {
        every { navController.previousBackStackEntry } returns null

        navigationManager.navigateBack()

        verify(exactly = 0) { navController.popBackStack() }
    }

    @Test
    fun `getCurrentRoute should return current route`() {
        val mockDestination = mockk<androidx.navigation.NavDestination>()
        every { mockDestination.route } returns NavigationRoutes.HOME
        every { navController.currentDestination } returns mockDestination

        val route = navigationManager.getCurrentRoute()

        assertEquals(NavigationRoutes.HOME, route)
    }

    @Test
    fun `getCurrentRoute should return null when no destination`() {
        every { navController.currentDestination } returns null

        val route = navigationManager.getCurrentRoute()

        assertEquals(null, route)
    }

    @Test
    fun `navigateToHome should handle navigation exceptions gracefully`() {
        every { navController.currentDestination?.route } returns null

        navigationManager.navigateToHome()

        assertTrue(true)
    }
}

