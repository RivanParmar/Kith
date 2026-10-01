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
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import com.kith.core.designsystem.icon.KithIcons
import com.kith.core.designsystem.theme.KithExtendedColors
import com.kith.core.designsystem.theme.KithTheme

@Composable
fun PostHistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: PostHistoryViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onPostClick: (String) -> Unit = {},
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
        onBackClick = onBackClick,
        modifier = modifier,
        onPostClick = onPostClick,
    )
}

@Composable
internal fun PostHistoryScreen(
    uiState: PostHistoryUiState,
    selectedTab: PostHistoryTab,
    isSyncing: Boolean,
    modifier: Modifier = Modifier,
    onTabSelected: (PostHistoryTab) -> Unit,
    onSync: () -> Unit,
    onBackClick: () -> Unit = {},
    onPostClick: (String) -> Unit = {},
) {
    val extendedColors = KithTheme.extendedColors

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
                        navigationIcon = KithIcons.ArrowBack,
                        navigationIconContentDescription = "Back",
                        onNavigationClick = onBackClick,
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
                            .padding(top = 16.dp)
                            .background(MaterialTheme.colorScheme.background) // Themed Background
                    ) {
                        SegmentedTabControl(
                            selectedTab = selectedTab,
                            extendedColors = extendedColors, // PASSED DOWN
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
                                PostHistoryCard(
                                    post = post,
                                    extendedColors = extendedColors,
                                    onClick = onPostClick,
                                )
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
    extendedColors: KithExtendedColors, // REPLACED STATIC COLORS
    onTabSelected: (PostHistoryTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = PostHistoryTab.entries
    val selectedIndex = tabs.indexOf(selectedTab)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(extendedColors.tabTrackBg, RoundedCornerShape(24.dp))
            .padding(4.dp)
    ) {
        BoxWithConstraints(modifier = Modifier.matchParentSize()) {
            val tabWidth = maxWidth / tabs.size
            val indicatorOffset by animateDpAsState(
                targetValue = tabWidth * selectedIndex,
                animationSpec = spring(dampingRatio = 0.75f, stiffness = 400f),
                label = "indicator_offset"
            )

            Box(
                modifier = Modifier
                    .offset(x = indicatorOffset)
                    .width(tabWidth)
                    .fillMaxHeight()
                    .background(extendedColors.tabPillBg, RoundedCornerShape(20.dp))
            )
        }

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { tab ->
                val isSelected = selectedTab == tab

                val animatedTextColor by animateColorAsState(
                    targetValue = if (isSelected) extendedColors.tabTextSelected else extendedColors.tabTextUnselected,
                    label = "tab_text_color"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(20.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onTabSelected(tab) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab.title,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        color = animatedTextColor
                    )
                }
            }
        }
    }
}

@Composable
private fun PostHistoryCard(
    post: Post,
    extendedColors: KithExtendedColors,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit = {},
) {
    val isActive = post.status == "OPEN" || post.status == "DRAFT"

    val badgeColor = if (isActive) extendedColors.historyBadgeActiveBg else extendedColors.historyBadgeInactiveBg
    val badgeTextColor = if (isActive) extendedColors.historyBadgeActiveText else extendedColors.historyBadgeInactiveText
    val badgeText = post.status.replace("_", " ")

    val xpColor = if (isActive) extendedColors.historyXpActiveText else extendedColors.historyXpInactiveText
    val xpText = if (isActive) "${post.reward} XP Offered" else "${post.reward} XP Earned"

    val timeAgo = DateUtils.getRelativeTimeSpanString(
        post.createdAt.toEpochMilliseconds(),
        System.currentTimeMillis(),
        DateUtils.MINUTE_IN_MILLIS
    ).toString()

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = extendedColors.historyCardBg),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, extendedColors.historyCardBorder),
        onClick = { onClick(post.id) },
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

                Text(
                    text = timeAgo,
                    fontSize = 12.sp,
                    color = extendedColors.historyTimeText
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = post.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = extendedColors.historyTitleText
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
                    color = extendedColors.historySubtitleText
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
