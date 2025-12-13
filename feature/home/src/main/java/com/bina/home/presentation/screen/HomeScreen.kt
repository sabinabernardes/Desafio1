package com.bina.home.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberAsyncImagePainter
import com.bina.core.designsystem.colors.ColorBackground
import com.bina.core.designsystem.colors.ColorPrimary
import com.bina.core.designsystem.components.ShimmerUserListLoading
import com.bina.core.designsystem.components.UserCard
import com.bina.core.designsystem.dimens.Dimens
import com.bina.core.designsystem.picpaytheme.PicPayTheme
import com.bina.core.designsystem.typography.Typography
import com.bina.home.R
import com.bina.home.domain.model.User
import com.bina.home.presentation.viewmodel.HomeUiState
import com.bina.home.presentation.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeRoute() {
    HomeScreen()
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    HomeScreenContent(
        uiState = uiState,
        isRefreshing = isRefreshing,
        onRetry = { viewModel.refresh() }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    isRefreshing: Boolean = false,
    onRetry: () -> Unit,
) {
    val pullRefreshState = rememberPullRefreshState(isRefreshing, { onRetry() })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorPrimary)
            .pullRefresh(pullRefreshState)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (uiState) {
                is HomeUiState.Loading -> LoadingSection()
                is HomeUiState.Error -> ErrorSection(
                    message = uiState.message,
                    onRetry = onRetry,
                    isRefreshing = isRefreshing
                )
                is HomeUiState.Success -> {
                    if (uiState.users.isNotEmpty()) {
                        UsersSection(users = uiState.users)
                    } else {
                        EmptySection(onRefresh = onRetry)
                    }
                }
            }
        }

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .semantics {
                    contentDescription = if (isRefreshing) {
                        "Atualizando contatos"
                    } else {
                        "Puxe para atualizar contatos"
                    }
                },
            backgroundColor = ColorPrimary,
            contentColor = ColorBackground
        )
    }
}

@Composable
private fun LoadingSection() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorPrimary)
    ) {
        Text(
            text = stringResource(id = R.string.home_title_contacts),
            style = Typography.displayLarge,
            modifier = Modifier
                .padding(
                    top = Dimens.spacing40,
                    bottom = Dimens.spacing32,
                    start = Dimens.spacing16,
                    end = Dimens.spacing16
                )
                .fillMaxWidth()
        )

        ShimmerUserListLoading(
            modifier = Modifier.padding(Dimens.spacing16),
            itemCount = 5
        )
    }
}

@Composable
private fun ErrorSection(
    message: String,
    onRetry: () -> Unit,
    isRefreshing: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBackground),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message, style = Typography.bodyLarge)
        Spacer(Modifier.height(Dimens.spacing16))
        Button(
            onClick = onRetry,
            enabled = !isRefreshing
        ) {
            if (isRefreshing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(Dimens.progressIndicatorSize),
                    color = ColorPrimary,
                    strokeWidth = Dimens.progressIndicatorStrokeWidth
                )
                Spacer(Modifier.width(Dimens.spacing8))
            }
            Text(stringResource(id = R.string.home_retry_button))
        }
    }
}

@Composable
private fun UsersSection(
    users: List<UserUi>
) {
    Text(
        text = stringResource(id = R.string.home_title_contacts),
        style = Typography.displayLarge,
        modifier = Modifier
            .padding(
                top = Dimens.spacing40,
                bottom = Dimens.spacing32,
                start = Dimens.spacing16,
                end = Dimens.spacing16
            )
            .fillMaxWidth()
    )

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(Dimens.spacing2),
        modifier = Modifier
            .fillMaxSize()
            .background(ColorPrimary)
    ) {
        items(
            items = users,
            key = { it.id }
        ) { user ->
            UserCard(
                avatar = rememberAsyncImagePainter(user.imageUrl),
                name = user.name,
                username = user.username,
            )
        }
    }
}

@Composable
private fun EmptySection(onRefresh: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorPrimary),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.home_empty_message),
            style = Typography.bodyLarge
        )
        Spacer(Modifier.height(Dimens.spacing24))
        Button(onClick = onRefresh) {
            Text(stringResource(id = R.string.home_empty_button))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    PicPayTheme {
        val users = listOf(
            User(
                img = "https://randomuser.me/api/portraits/men/1.jpg",
                name = "Jaiminho o Carteiro",
                id = "1",
                username = "Evitar a Fadiga"
            ),
            User(
                img = "https://randomuser.me/api/portraits/women/2.jpg",
                name = "Chiquinha",
                id = "2",
                username = "Pois é, pois é, pois é"
            )
        ).map { it.toUi() }
        HomeScreenContent(
            uiState = HomeUiState.Success(users),
            isRefreshing = false,
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenLoadingPreview() {
    PicPayTheme {
        HomeScreenContent(
            uiState = HomeUiState.Loading,
            isRefreshing = false,
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenEmptyPreview() {
    PicPayTheme {
        HomeScreenContent(
            uiState = HomeUiState.Success(emptyList()),
            isRefreshing = false,
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenErrorPreview() {
    PicPayTheme {
        HomeScreenContent(
            uiState = HomeUiState.Error("Erro de rede. Tente novamente."),
            isRefreshing = false,
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenShimmerLoadingPreview() {
    PicPayTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ColorPrimary)
                .padding(Dimens.spacing16)
        ) {
            Text(
                text = stringResource(id = R.string.home_title_contacts),
                style = Typography.displayLarge,
                modifier = Modifier
                    .padding(bottom = Dimens.spacing16)
                    .fillMaxWidth()
            )
            ShimmerUserListLoading(itemCount = 4)
        }
    }
}

