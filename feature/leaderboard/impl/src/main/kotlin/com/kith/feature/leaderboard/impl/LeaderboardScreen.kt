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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kith.core.designsystem.theme.KithTheme

val PrimaryBlue = Color(0xFF2563EB)
val LightBlueBg = Color(0xFFEFF6FF)
val ActiveCardBorder = Color(0xFF3B82F6)
val TextDark = Color(0xFF0F172A)
val TextMuted = Color(0xFF64748B)
val TabBackground = Color(0xFFF1F5F9)
val DividerColor = Color(0xFFE2E8F0)

val GoldColor = Color(0xFFF59E0B)
val SilverColor = Color(0xFF94A3B8)
val BronzeColor = Color(0xFFB45309)


val initialLeaderboardUsers = listOf(
    LeaderboardUser(
        id = "aris",
        name = "Aris Thorne",
        department = "Software Engineering",
        avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200&auto=format&fit=crop&q=80",
        xp = 3450,
        tasks = 58,
        rating = 4.95f,
        streakDays = 14,
        bio = "Senior algorithm specialist and systems architect."
    ),
    LeaderboardUser(
        id = "maya",
        name = "Maya Lin",
        department = "Data Science",
        avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200&auto=format&fit=crop&q=80",
        xp = 2900,
        tasks = 45,
        rating = 4.88f,
        streakDays = 9,
        bio = "Neural network modeling and statistical learning."
    ),
    LeaderboardUser(
        id = "emily",
        name = "Emily Watson",
        department = "Product Design",
        avatarUrl = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=200&auto=format&fit=crop&q=80",
        xp = 2750,
        tasks = 41,
        rating = 4.91f,
        streakDays = 12,
        bio = "Lead product designer crafting accessible user systems."
    ),
    LeaderboardUser(
        id = "brandon",
        name = "Brandon Miller",
        department = "Computer Science",
        avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150&auto=format&fit=crop&q=80",
        xp = 2400,
        tasks = 36,
        rating = 4.79f,
        streakDays = 7,
        bio = "Distributed systems researcher and compiler enthusiast."
    ),
    LeaderboardUser(
        id = "alex",
        name = "Alex Rivera",
        department = "Mobile & Cloud",
        avatarUrl = "https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?w=150&auto=format&fit=crop&q=80",
        xp = 1450,
        tasks = 25,
        rating = 4.72f,
        streakDays = 5,
        isCurrentUser = true,
        bio = "Building Kotlin Android & Jetpack Compose native experiences."
    ),
    LeaderboardUser(
        id = "sophia",
        name = "Sophia Chen",
        department = "Biochemistry • •",
        avatarUrl = "https://images.unsplash.com/photo-1580489944761-15a19d654956?w=150&auto=format&fit=crop&q=80",
        xp = 1380,
        tasks = 22,
        rating = 4.65f,
        streakDays = 4,
        bio = "Computational biology, genetics, and molecular dynamics."
    ),
    LeaderboardUser(
        id = "jordan",
        name = "Jordan Taylor",
        department = "Economics",
        avatarUrl = "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=150&auto=format&fit=crop&q=80",
        xp = 1290,
        tasks = 19,
        rating = 4.58f,
        streakDays = 3,
        bio = "Quantitative finance and macro econometric models."
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    modifier: Modifier = Modifier,
) {
    var selectedTab by remember { mutableStateOf(LeaderboardTab.BY_XP) }
    var selectedTimeframe by remember { mutableStateOf(Timeframe.WEEKLY) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var usersList by remember { mutableStateOf(initialLeaderboardUsers) }
    var selectedUserForDetail by remember { mutableStateOf<LeaderboardUser?>(null) }

    val sortedUsers = remember(usersList, selectedTab, selectedTimeframe) {
        usersList.sortedByDescending { user ->
            when (selectedTab) {
                LeaderboardTab.BY_XP -> (user.xp * selectedTimeframe.multiplier)
                LeaderboardTab.BY_TASKS -> (user.tasks * selectedTimeframe.multiplier)
                LeaderboardTab.BY_RATING -> user.rating
            }
        }
    }

    val filteredUsers = remember(sortedUsers, searchQuery) {
        if (searchQuery.isBlank()) sortedUsers
        else sortedUsers.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.department.contains(searchQuery, ignoreCase = true)
        }
    }

    val top1 = sortedUsers.getOrNull(0)
    val top2 = sortedUsers.getOrNull(1)
    val top3 = sortedUsers.getOrNull(2)

    val restUsers = filteredUsers.filter { user ->
        val rank = sortedUsers.indexOfFirst { it.id == user.id } + 1
        rank > 3
    }

    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.White,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    SegmentedTabs(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it }
                    )
                }

                if (top1 != null && top2 != null && top3 != null) {
                    item {
                        DynamicPodiumSection(
                            first = top1,
                            second = top2,
                            third = top3,
                            selectedTab = selectedTab,
                            multiplier = selectedTimeframe.multiplier,
                            onUserClick = { selectedUserForDetail = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, bottom = 12.dp)
                        )
                    }
                }

                items(restUsers, key = { it.id }) { user ->
                    val actualRank = sortedUsers.indexOfFirst { it.id == user.id } + 1
                    RankItemRow(
                        user = user,
                        rank = actualRank,
                        selectedTab = selectedTab,
                        multiplier = selectedTimeframe.multiplier,
                        onClick = { selectedUserForDetail = user }
                    )
                }

                if (restUsers.isEmpty() && searchQuery.isNotBlank()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No participants found matching \"$searchQuery\"",
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            selectedUserForDetail?.let { user ->
                val userRank = sortedUsers.indexOfFirst { it.id == user.id } + 1
                ParticipantBottomSheet(
                    user = user,
                    rank = userRank,
                    onDismiss = { selectedUserForDetail = null }
                )
            }
        }
    }
}

@Composable
private fun SegmentedTabs(
    selectedTab: LeaderboardTab,
    onTabSelected: (LeaderboardTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(TabBackground, CircleShape)
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LeaderboardTab.entries.forEach { tab ->
            val isSelected = tab == selectedTab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .then(
                        if (isSelected) {
                            Modifier
                                .shadow(
                                    elevation = 2.dp,
                                    shape = CircleShape,
                                    clip = false
                                )
                                .background(Color.White, CircleShape)
                        } else {
                            // Explicitly transparent for unselected tabs
                            Modifier.background(Color.Transparent, CircleShape)
                        }
                    )
                    .clip(CircleShape)
                    .clickable(
                        // Removes the grey ripple / hover / focus overlay completely:
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onTabSelected(tab) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tab.title,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) PrimaryBlue else TextMuted
                )
            }
        }
    }
}


@Composable
private fun DynamicPodiumSection(
    first: LeaderboardUser,
    second: LeaderboardUser,
    third: LeaderboardUser,
    selectedTab: LeaderboardTab,
    multiplier: Float,
    onUserClick: (LeaderboardUser) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        Spacer(modifier = Modifier.width(25.dp))

        PodiumColumn(
            user = second,
            rankNumber = 2,
            scoreText = formatUserScore(second, selectedTab, multiplier),
            pillarHeight = 145.dp,
            pillarGradient = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF94A3B8),
                    Color(0xFFCBD5E1),
                    Color(0xFFF1F5F9).copy(alpha = 0.2f)
                )
            ),
            ringColor = SilverColor,
            medalBg = Color(0xFF94A3B8),
            avatarSize = 66.dp,
            onClick = { onUserClick(second) },
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(45.dp))

        PodiumColumn(
            user = first,
            rankNumber = 1,
            scoreText = formatUserScore(first, selectedTab, multiplier),
            pillarHeight = 195.dp,
            pillarGradient = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFF59E0B),
                    Color(0xFFFBBF24),
                    Color(0xFFFEF3C7).copy(alpha = 0.2f)
                )
            ),
            ringColor = GoldColor,
            medalBg = Color(0xFFF59E0B),
            avatarSize = 74.dp,
            onClick = { onUserClick(first) },
            modifier = Modifier.weight(1.05f)
        )

        Spacer(modifier = Modifier.width(45.dp))

        PodiumColumn(
            user = third,
            rankNumber = 3,
            scoreText = formatUserScore(third, selectedTab, multiplier),
            pillarHeight = 110.dp,
            pillarGradient = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFB45309),
                    Color(0xFFD97706),
                    Color(0xFFFED7AA).copy(alpha = 0.2f)
                )
            ),
            ringColor = BronzeColor,
            medalBg = Color(0xFFB45309),
            avatarSize = 66.dp,
            onClick = { onUserClick(third) },
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(25.dp))
    }
}

@Composable
private fun PodiumColumn(
    user: LeaderboardUser,
    rankNumber: Int,
    scoreText: String,
    pillarHeight: Dp,
    pillarGradient: Brush,
    ringColor: Color,
    medalBg: Color,
    avatarSize: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        // Avatar with colored ring & Ribbon Medal
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier.padding(bottom = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(avatarSize)
                    .border(
                        3.5.dp,
                        if (user.isCurrentUser) PrimaryBlue else ringColor,
                        CircleShape
                    )
                    .padding(3.dp)
                    .clip(CircleShape)
            ) {
            }

            // Ribbon Medal badge
            Box(
                modifier = Modifier
                    .offset(y = 6.dp)
                    .size(20.dp)
                    .background(medalBg, CircleShape)
                    .border(1.5.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = rankNumber.toString(),
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = user.name,
            fontWeight = FontWeight.Bold,
            fontSize = 13.5.sp,
            color = TextDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(3.dp))

        Box(
            modifier = Modifier
                .background(LightBlueBg, RoundedCornerShape(50))
                .padding(horizontal = 10.dp, vertical = 3.dp)
        ) {
            Text(
                text = scoreText,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Pedestal with smooth height animation
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(pillarHeight)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(pillarGradient)
        )
    }
}

@Composable
private fun RankItemRow(
    user: LeaderboardUser,
    rank: Int,
    selectedTab: LeaderboardTab,
    multiplier: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isHighlighted = user.isCurrentUser

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .then(
                if (isHighlighted) {
                    Modifier
                        .border(1.8.dp, ActiveCardBorder, RoundedCornerShape(16.dp))
                        .background(Color(0xFFF6FAFF))
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                } else {
                    Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                }
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = rank.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = if (isHighlighted) PrimaryBlue else TextDark,
                modifier = Modifier.width(28.dp),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isHighlighted) "You (Tap for details)" else user.department,
                    fontSize = 12.sp,
                    color = if (isHighlighted) PrimaryBlue else TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = formatUserScore(user, selectedTab, multiplier),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = PrimaryBlue
            )
        }
    }
}

@Composable
private fun ParticipantBottomSheet(
    user: LeaderboardUser,
    rank: Int,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = PrimaryBlue, fontWeight = FontWeight.Bold)
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Rank #$rank Profile", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(user.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(user.department, color = TextMuted, fontSize = 13.sp)
                    }
                }
                HorizontalDivider(color = DividerColor)
                Text("• Total XP: ${user.xp.toString()} XP", fontSize = 13.sp)
                Text("• Tasks Completed: ${user.tasks}", fontSize = 13.sp)
                Text("• Rating Score: ${user.rating} ★", fontSize = 13.sp)
                Text("• Streak: ${user.streakDays} days active 🔥", fontSize = 13.sp)
                if (user.bio.isNotBlank()) {
                    Text(
                        user.bio,
                        fontStyle = FontStyle.Italic,
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }
        }
    )
}


// Utility function to format scores based on tab & timeframe multiplier
fun formatUserScore(user: LeaderboardUser, tab: LeaderboardTab, multiplier: Float): String {
    return when (tab) {
        LeaderboardTab.BY_XP -> {
            val total = (user.xp * multiplier).toInt()
            "$total XP"
        }

        LeaderboardTab.BY_TASKS -> {
            val total = (user.tasks * multiplier).toInt()
            "$total Tasks"
        }

        LeaderboardTab.BY_RATING -> {
            "%.2f ★".format(user.rating)
        }
    }
}

@Preview
@Composable
private fun LeaderboardPreview() {
    KithTheme {
        LeaderboardScreen()
    }
}
