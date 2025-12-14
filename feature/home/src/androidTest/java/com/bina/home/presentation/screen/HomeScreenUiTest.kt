package com.bina.home.presentation.screen

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.performClick
import com.bina.core.designsystem.picpaytheme.PicPayTheme
import com.bina.home.domain.model.User
import com.bina.home.presentation.viewmodel.HomeUiState
import org.junit.Rule
import org.junit.Test

class HomeScreenUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showsLoadingState_whenLoading() {
        composeTestRule.setContent {
            PicPayTheme {
                HomeScreenContent(
                    uiState = HomeUiState.Loading,
                    isRefreshing = false,
                    onRetry = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("loadingSection").assertIsDisplayed()
    }

    @Test
    fun showsUserList_whenSuccess() {
        val users = listOf(
            User("img", "João Silva", "1", "joaosilva").toUi(),
            User("img2", "Maria Santos", "2", "mariasantos").toUi()
        )

        composeTestRule.setContent {
            PicPayTheme {
                HomeScreenContent(
                    uiState = HomeUiState.Success(users),
                    isRefreshing = false,
                    onRetry = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("usersSection").assertIsDisplayed()
        composeTestRule.onNodeWithText("João Silva").assertIsDisplayed()
        composeTestRule.onNodeWithText("joaosilva").assertIsDisplayed()
    }

    @Test
    fun showsEmptyState_whenListIsEmpty() {
        composeTestRule.setContent {
            PicPayTheme {
                HomeScreenContent(
                    uiState = HomeUiState.Empty,
                    isRefreshing = false,
                    onRetry = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("emptySection").assertIsDisplayed()
    }

    @Test
    fun showsErrorState_whenError() {
        val errorMessage = "Erro de conexão"

        composeTestRule.setContent {
            PicPayTheme {
                HomeScreenContent(
                    uiState = HomeUiState.Error(errorMessage),
                    isRefreshing = false,
                    onRetry = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("errorSection").assertIsDisplayed()
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }

    @Test
    fun callsOnRetry_whenRetryButtonClicked() {
        var retryClicked = false

        composeTestRule.setContent {
            PicPayTheme {
                HomeScreenContent(
                    uiState = HomeUiState.Error("Erro"),
                    isRefreshing = false,
                    onRetry = { retryClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithTag("retryButton").performClick()
        assert(retryClicked)
    }
}
