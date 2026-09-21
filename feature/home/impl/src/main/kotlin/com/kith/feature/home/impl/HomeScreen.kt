package com.kith.feature.home.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kith.core.designsystem.component.KithMediumTopAppBar
import com.kith.core.designsystem.icon.KithIcons
import com.kith.core.designsystem.theme.KithTheme
import com.kith.core.model.data.Post
import com.kith.core.model.data.WalletData
import com.kith.core.ui.PostPreviewParameterProvider
import com.kith.core.ui.PostsFeedUiState
import com.kith.core.ui.WalletCard
import com.kith.core.ui.WalletUiState
import com.kith.core.ui.postsFeed
import com.kith.feature.home.api.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val feedState by viewModel.feedState.collectAsStateWithLifecycle()
    val walletUiState by viewModel.walletState.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()

    HomeScreen(
        isSyncing = isSyncing,
        feedState = feedState,
        walletUiState = walletUiState,
        onSync = viewModel::sync,
        modifier = modifier,
    )
}

@Composable
internal fun HomeScreen(
    isSyncing: Boolean,
    feedState: PostsFeedUiState,
    walletUiState: WalletUiState,
    modifier: Modifier = Modifier,
    onSync: () -> Unit = {},
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
//            Text(
//                text = "KITH",
//                fontSize = 32.sp,
//                fontWeight = FontWeight.Bold,
//                color = Color.Black,
//                letterSpacing = 1.5.sp,
//                modifier = Modifier.padding(horizontal = 24.dp, vertical = 30.dp),
//            )
            KithMediumTopAppBar(
                titleRes = R.string.feature_home_api_app_title,
                navigationIcon = null,
                navigationIconContentDescription = null,
                actionIcon = KithIcons.Notifications,
                actionIconContentDescription = "Notifications",
                scrollBehavior = scrollBehavior,
            )
        }
    ) { padding ->

        PullToRefreshBox(
            isRefreshing = isSyncing,
            onRefresh = onSync,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            LazyVerticalGrid (
                modifier = Modifier.fillMaxSize(),
                columns = GridCells.Adaptive(300.dp),
                contentPadding = PaddingValues(start = 10.dp, end = 10.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    WalletCard(walletUiState = walletUiState)
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
                            top = 24.dp,
                            bottom = 8.dp,
                        )
                    )
                }

                postsFeed(feedState = feedState)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview(
    @PreviewParameter(PostPreviewParameterProvider::class)
    posts: List<Post>,
) {
    KithTheme {
        HomeScreen(
            isSyncing = false,
            feedState = PostsFeedUiState.Success(
                feed = posts,
            ),
            walletUiState = WalletUiState.Success(
                WalletData(balance = 1450,
                    level = 3,
                    nextTierXp = 2000,)
            ),
            onSync = {},
        )
    }
}