package com.kith.feature.profile.impl

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kith.core.designsystem.component.KithMediumTopAppBar
import com.kith.core.designsystem.component.LoadingWheel
import com.kith.core.model.data.Post
import com.kith.feature.profile.api.R
import android.text.format.DateUtils

@Composable
fun PostHistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: PostHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()

    PostHistoryScreen(
        uiState = uiState,
        selectedTab = selectedTab,
        isSyncing = isSyncing,
        onTabSelected = viewModel::setTab,
        onSync = viewModel::sync,
        modifier = modifier
    )
}

@Composable
internal fun PostHistoryScreen(
    uiState: PostHistoryUiState,
    selectedTab: PostHistoryTab,
    isSyncing: Boolean,
    onTabSelected: (PostHistoryTab) -> Unit,
    onSync: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        is PostHistoryUiState.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingWheel(contentDesc = "Loading Post History")
            }
        }
        is PostHistoryUiState.Error -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = uiState.message, color = Color.Red)
            }
        }
        is PostHistoryUiState.Success -> {
            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

            Scaffold(
                modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    KithMediumTopAppBar(
                        titleRes = R.string.feature_profile_api_post_history_title,
                        navigationIcon = null,
                        navigationIconContentDescription = null,
                        actionIcon = null,
                        actionIconContentDescription = null,
                        scrollBehavior = scrollBehavior,
                    )
                }
            ) { padding ->
                PullToRefreshBox(
                    isRefreshing = isSyncing,
                    onRefresh = onSync,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                    ) {
                        SegmentedTabControl(
                            selectedTab = selectedTab,
                            onTabSelected = onTabSelected,
                            modifier = Modifier
                                .padding(horizontal = 24.dp)
                                .padding(bottom = 16.dp)
                        )

                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                items = uiState.posts,
                                key = { it.id }
                            ) { post ->
                                PostHistoryCard(post = post)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SegmentedTabControl(
    selectedTab: PostHistoryTab,
    onTabSelected: (PostHistoryTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFF1F5F9))
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        PostHistoryTab.entries.forEach { tab ->
            val isSelected = selectedTab == tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) Color.White else Color.Transparent)
                    .clickable { onTabSelected(tab) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tab.title,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                    color = if (isSelected) Color(0xFF3B82F6) else Color(0xFF64748B)
                )
            }
        }
    }
}

@Composable
private fun PostHistoryCard(
    post: Post,
    modifier: Modifier = Modifier
) {
    val isActive = post.status == "OPEN" || post.status == "DRAFT"

    val badgeColor = if (isActive) Color(0xFFEFF6FF) else Color(0xFFFFFBEB)
    val badgeTextColor = if (isActive) Color(0xFF3B82F6) else Color(0xFFD97706)

    val badgeText = post.status.replace("_", " ")

    val xpColor = if (isActive) Color(0xFF64748B) else Color(0xFF3B82F6)
    val xpText = if (isActive) "${post.reward} XP Offered" else "${post.reward} XP Earned"

    // ADDED: Automatically formats the time to "10 min ago", "Yesterday", "2 days ago", etc.
    val timeAgo = DateUtils.getRelativeTimeSpanString(
        post.createdAt.toEpochMilliseconds(),
        System.currentTimeMillis(),
        DateUtils.MINUTE_IN_MILLIS
    ).toString()

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(badgeColor)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeTextColor
                    )
                }

                // ADDED: Display the dynamic timestamp here
                Text(
                    text = timeAgo,
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = post.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = post.community.name,
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )

                Text(
                    text = xpText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = xpColor
                )
            }
        }
    }
}



