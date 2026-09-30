package com.kith.feature.leaderboard.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.kith.core.designsystem.component.KithMediumTopAppBar
import com.kith.core.designsystem.theme.KithTheme
import com.kith.core.designsystem.theme.OutfitFontFamily
import com.kith.core.model.data.UserProfile
import com.kith.core.ui.ProfileAvatar
import com.kith.core.ui.RingProfileAvatar

private object LeaderboardColors {
    val primary = Color(0xFF2563EB)
    val primaryBg = Color(0xFFEFF6FF)
    val textDark = Color(0xFF0F172A)
    val textMuted = Color(0xFF64748B)
    val tabBg = Color(0xFFF1F5F9)
    val gold = Color(0xFFF59E0B)
    val silver = Color(0xFF94A3B8)
    val bronze = Color(0xFFB45309)
}

private data class PodiumStyle(
    val rank: Int,
    val height: Dp,
    val avatarSize: Dp,
    val accent: Color,
    val gradient: Brush,
)

private val podiumStyles = listOf(
    PodiumStyle(
        rank = 2, height = 145.dp, avatarSize = 66.dp, accent = LeaderboardColors.silver,
        gradient = Brush.verticalGradient(listOf(Color(0xFF94A3B8), Color(0xFFCBD5E1), Color(0xFFF1F5F9).copy(alpha = 0.2f))),
    ),
    PodiumStyle(
        rank = 1, height = 195.dp, avatarSize = 74.dp, accent = LeaderboardColors.gold,
        gradient = Brush.verticalGradient(listOf(Color(0xFFF59E0B), Color(0xFFFBBF24), Color(0xFFFEF3C7).copy(alpha = 0.2f))),
    ),
    PodiumStyle(
        rank = 3, height = 110.dp, avatarSize = 66.dp, accent = LeaderboardColors.bronze,
        gradient = Brush.verticalGradient(listOf(Color(0xFFB45309), Color(0xFFD97706), Color(0xFFFED7AA).copy(alpha = 0.2f))),
    ),
)

private fun UserProfile.getFormattedScore(tab: LeaderboardTab): String {
    return when (tab) {
        LeaderboardTab.BY_XP -> "$xp XP"
        LeaderboardTab.BY_TASKS -> "$problemsSolved Solved"
        LeaderboardTab.BY_RATING -> "$rating ★"
    }
}

@Composable
fun LeaderboardScreen(
    modifier: Modifier = Modifier,
    viewModel: LeaderboardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentTab by viewModel.selectedSortBy.collectAsStateWithLifecycle()
    LeaderboardContent(
        uiState = uiState,
        currentTab = currentTab,
        onTabChanged = viewModel::onTabChanged,
        modifier = modifier,
    )
}

@Composable
private fun LeaderboardContent(
    uiState: LeaderboardUiState,
    modifier: Modifier = Modifier,
    currentTab: LeaderboardTab = LeaderboardTab.BY_XP,
    onTabChanged: (LeaderboardTab) -> Unit = {},
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            KithMediumTopAppBar(
                titleRes = com.kith.feature.leaderboard.api.R.string.feature_leaderboard_api_title,
                navigationIcon = null,
                navigationIconContentDescription = null,
                actionIcon = null,
                actionIconContentDescription = null,
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.ExtraBold
            )
        }
    ) { padding ->
        when (uiState) {
            is LeaderboardUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = LeaderboardColors.primary)
                }
            }
            is LeaderboardUiState.Success -> {
                val podium = remember(uiState.users) { uiState.users.take(3) }
                val rows = remember(uiState.users) { uiState.users.drop(3) }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item {
                        SegmentedTabs(
                            selectedTab = currentTab,
                            onTabSelected = onTabChanged,
                        )
                    }

                    if (podium.isNotEmpty()) {
                        item {
                            PodiumRow(
                                podium = podium,
                                currentTab = currentTab,
                                onUserClick = { },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp, bottom = 12.dp),
                            )
                        }
                    }

                    itemsIndexed(rows, key = { _, user -> user.id }) { index, user ->
                        RankItemRow(
                            user = user,
                            rank = index + 4,
                            currentTab = currentTab,
                            onClick = { },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SegmentedTabs(
    selectedTab: LeaderboardTab,
    onTabSelected: (LeaderboardTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(LeaderboardColors.tabBg, CircleShape)
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LeaderboardTab.entries.forEach { tab ->
            val isSelected = tab == selectedTab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(if (isSelected) Color.White else Color.Transparent, CircleShape)
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onTabSelected(tab)
                    }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = tab.title,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) LeaderboardColors.primary else LeaderboardColors.textMuted,
                )
            }
        }
    }
}

@Composable
private fun PodiumRow(
    podium: List<UserProfile>,
    currentTab: LeaderboardTab,
    onUserClick: (UserProfile) -> Unit,
    modifier: Modifier = Modifier,
) {
    val ordered = podiumStyles.mapNotNull { style ->
        podium.getOrNull(style.rank - 1)?.let { user -> style to user }
    }

    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 25.dp),
        horizontalArrangement = Arrangement.spacedBy(45.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        ordered.forEach { (style, user) ->
            PodiumColumn(
                user = user,
                style = style,
                currentTab = currentTab,
                onClick = { onUserClick(user) },
                modifier = Modifier.weight(if (style.rank == 1) 1.05f else 1f),
            )
        }
    }
}

@Composable
private fun PodiumColumn(
    user: UserProfile,
    style: PodiumStyle,
    currentTab: LeaderboardTab,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
    ) {
        Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.padding(bottom = 6.dp)) {
            RingProfileAvatar(
                isPremium = user.isPremium,
                avatarSize = style.avatarSize,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(if (user.isPremium) 0.dp else 3.5.dp, style.accent, CircleShape)
                        .padding(if (user.isPremium) 0.dp else 3.dp)
                        .clip(CircleShape)
                        .background(LeaderboardColors.tabBg),
                    contentAlignment = Alignment.Center,
                ) {
                    if (!user.profileImageUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = user.profileImageUrl,
                            contentDescription = user.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = user.name.take(1).uppercase(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = LeaderboardColors.textDark,
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .offset(y = 6.dp)
                    .size(20.dp)
                    .background(style.accent, CircleShape)
                    .border(1.5.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(style.rank.toString(), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(6.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 4.dp),
        ) {
            Text(
                text = user.name,
                fontWeight = FontWeight.Bold,
                fontSize = 13.5.sp,
                color = LeaderboardColors.textDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false),
            )
            if (user.isPremium) {
                Spacer(Modifier.width(2.dp))
                Text(
                    text = "★",
                    fontSize = 11.sp,
                    color = LeaderboardColors.gold,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Spacer(Modifier.height(3.dp))
        Box(
            Modifier
                .background(LeaderboardColors.primaryBg, RoundedCornerShape(50))
                .padding(horizontal = 10.dp, vertical = 3.dp)
        ) {
            Text(
                text = user.getFormattedScore(currentTab),
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = LeaderboardColors.primary,
            )
        }
        Spacer(Modifier.height(10.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(style.height)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(style.gradient)
        )
    }
}

@Composable
private fun RankItemRow(
    user: UserProfile,
    rank: Int,
    currentTab: LeaderboardTab,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 8.dp),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = rank.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = LeaderboardColors.textDark,
                modifier = Modifier.width(28.dp),
            )
            Spacer(Modifier.width(8.dp))
            ProfileAvatar(
                userProfile = user,
                avatarSize = 40.dp,
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = user.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = LeaderboardColors.textDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    if (user.isPremium) {
                        Spacer(Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(LeaderboardColors.gold.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "PRO",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = LeaderboardColors.gold,
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = user.getFormattedScore(currentTab),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = LeaderboardColors.primary,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LeaderboardPreview() {
    val sampleUsers = listOf(
        UserProfile(
            id = "1",
            name = "Alex",
            profileImageUrl = null,
            bio = "Senior Android Engineer.",
            xp = 12500,
            rating = 4.9f,
            problemsAsked = 12,
            problemsSolved = 145,
            isPremium = true
        ),
        UserProfile(
            id = "2",
            name = "Sarah",
            profileImageUrl = null,
            bio = "Backend developer.",
            xp = 8400,
            rating = 4.7f,
            problemsAsked = 24,
            problemsSolved = 82,
            isPremium = false
        ),
        UserProfile(
            id = "3",
            name = "John",
            profileImageUrl = null,
            bio = null,
            xp = 3200,
            rating = 4.5f,
            problemsAsked = 45,
            problemsSolved = 15,
            isPremium = false
        ),
        UserProfile(
            id = "4",
            name = "Emily",
            profileImageUrl = null,
            bio = "UI/UX Designer.",
            xp = 15600,
            rating = 4.2f,
            problemsAsked = 8,
            problemsSolved = 210,
            isPremium = true
        ),
        UserProfile(
            id = "5",
            name = "Michael",
            profileImageUrl = null,
            bio = "Getting started!",
            xp = 450,
            rating = 4.0f,
            problemsAsked = 3,
            problemsSolved = 2,
            isPremium = false
        ),
    )
    KithTheme {
        LeaderboardContent(
            uiState = LeaderboardUiState.Success(users = sampleUsers),
            currentTab = LeaderboardTab.BY_XP,
        )
    }
}