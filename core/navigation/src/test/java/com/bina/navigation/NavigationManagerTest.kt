package com.bina.navigation

import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NavigationManagerTest {

    private lateinit var navController: NavHostController
    private lateinit var navManager: NavigationManager

    @Before
    fun setup() {
        navController = mockk(relaxed = true)
        navManager = NavigationManager(navController)
    }

    @Test
    fun `navigateToHome returns false when already on HOME and does not navigate`() {
        // given
        val destination: NavDestination = mockk()
        every { destination.route } returns NavigationRoutes.HOME
        every { navController.currentDestination } returns destination

        // when
        val result = navManager.navigateToHome()

        // then
        assertFalse(result)
        verify(exactly = 0) { navController.navigate(NavigationRoutes.HOME) }
    }

    @Test
    fun `navigateToHome navigates when not on HOME`() {
        val destination: NavDestination = mockk()
        every { destination.route } returns "OTHER"
        every { navController.currentDestination } returns destination

        val result = navManager.navigateToHome()

        assertTrue(result)
        verify(exactly = 1) {
            navController.navigate(
                NavigationRoutes.HOME,
                any<androidx.navigation.NavOptionsBuilder.() -> Unit>()
            )
        }
    }

    @Test
    fun `navigateToHome calls onError and returns false when navigate throws`() {
        val errors = mutableListOf<Throwable>()
        navManager = NavigationManager(navController) { errors.add(it) }

        val destination: NavDestination = mockk()
        every { destination.route } returns "OTHER"
        every { navController.currentDestination } returns destination

        every {
            navController.navigate(
                NavigationRoutes.HOME,
                any<androidx.navigation.NavOptionsBuilder.() -> Unit>()
            )
        } throws IllegalStateException("boom")

        val result = navManager.navigateToHome()

        assertFalse(result)
        assertEquals(1, errors.size)
        assertEquals("boom", errors.first().message)
    }

    @Test
    fun `navigateBack delegates to popBackStack`() {
        // given
        every { navController.popBackStack() } returns true

        // when
        val result = navManager.navigateBack()

        // then
        assertTrue(result)
        verify(exactly = 1) { navController.popBackStack() }
    }

    @Test
    fun `getCurrentRoute returns current destination route`() {
        // given
        val destination: NavDestination = mockk()
        every { destination.route } returns NavigationRoutes.HOME
        every { navController.currentDestination } returns destination

        // then
        assertEquals(NavigationRoutes.HOME, navManager.getCurrentRoute())
    }
}
