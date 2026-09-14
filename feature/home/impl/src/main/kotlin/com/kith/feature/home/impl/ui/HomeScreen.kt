package com.kith.feature.home.impl.ui
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kith.core.model.data.Post
import com.kith.core.ui.KithPreviewData
import com.kith.core.ui.PostCard
import com.kith.core.ui.WalletCard
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kith.core.designsystem.component.LoadingWheel



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    when (uiState) {
        is HomeUiState.Loading -> {
            LoadingWheel(contentDesc = "Loading Home", modifier = Modifier.fillMaxSize())
        }
        is HomeUiState.Error -> {
            Text(text = (uiState as HomeUiState.Error).message)
        }
        is HomeUiState.Success -> {
            val successState = uiState as HomeUiState.Success
            HomeScreen(
                currentBalance = successState.currentBalance,
                currentLevel = successState.currentLevel,
                nextTierXp = successState.nextTierXp,
                recentPosts = successState.recentPosts,
                isRefreshing = isRefreshing,
                onRefresh = viewModel::refreshPosts,
                modifier = modifier
            )
        }
    }
}



@Composable

fun HomeScreen(
    currentBalance: Int,
    currentLevel: Int,
    nextTierXp: Int,
    recentPosts: List<Post>,
    modifier: Modifier = Modifier,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {}
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            Text(
                text = "KITH",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 30.dp)
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item {
                    WalletCard(
                        currentBalance = currentBalance,
                        currentLevel = currentLevel,
                        nextTierXp = nextTierXp,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }

                item {
                    Text(
                        text = "Community Requests",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A),
                        modifier = Modifier.padding(
                            start = 24.dp,
                            end = 24.dp,
                            top = 32.dp,
                            bottom = 16.dp
                        )
                    )
                }

                items(
                    items = recentPosts,
                    key = { post -> post.id }
                ) { post ->
                    PostCard(
                        post = post
                    )
                }
            }
        }
    }
}



@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen(
            currentBalance = KithPreviewData.WALLET_BALANCE,
            currentLevel = KithPreviewData.WALLET_LEVEL,
            nextTierXp = KithPreviewData.NEXT_TIER_XP,
            recentPosts = KithPreviewData.posts,
            isRefreshing = false,
            onRefresh = {}
        )
    }
}