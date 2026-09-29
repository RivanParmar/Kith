package com.kith.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.kith.core.designsystem.icon.KithIcons
import com.kith.feature.browse.api.navigation.BrowseNavKey
import com.kith.feature.home.api.navigation.HomeNavKey
import com.kith.feature.leaderboard.api.navigation.LeaderboardNavKey
import com.kith.feature.post.api.navigation.CreatePostNavKey
import com.kith.feature.profile.api.navigation.ProfileNavKey
import com.kith.feature.browse.api.R as browseR
import com.kith.feature.home.api.R as homeR
import com.kith.feature.leaderboard.api.R as leaderboardR
import com.kith.feature.post.api.R as postR
import com.kith.feature.profile.api.R as profileR

data class TopLevelNavItem(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @StringRes val iconTextId: Int,
    @StringRes val titleTextId: Int,
)

val HOME = TopLevelNavItem(
    selectedIcon = KithIcons.Home,
    unselectedIcon = KithIcons.HomeOutlined,
    iconTextId = homeR.string.feature_home_api_title,
    titleTextId = homeR.string.feature_home_api_title,
)

val BROWSE = TopLevelNavItem(
    selectedIcon = KithIcons.Search,
    unselectedIcon = KithIcons.Search,
    iconTextId = browseR.string.feature_browse_api_title,
    titleTextId = browseR.string.feature_browse_api_title,
)

val POST = TopLevelNavItem(
    selectedIcon = KithIcons.Add,
    unselectedIcon = KithIcons.Add,
    iconTextId = postR.string.feature_post_api_title,
    titleTextId = postR.string.feature_post_api_title,
)

val LEADERBOARD = TopLevelNavItem(
    selectedIcon = KithIcons.Leaderboard,
    unselectedIcon = KithIcons.LeaderboardOutlined,
    iconTextId = leaderboardR.string.feature_leaderboard_api_title,
    titleTextId = leaderboardR.string.feature_leaderboard_api_title,
)

val PROFILE = TopLevelNavItem(
    selectedIcon = KithIcons.Person,
    unselectedIcon = KithIcons.PersonOutlined,
    iconTextId = profileR.string.feature_profile_api_title,
    titleTextId = profileR.string.feature_profile_api_title,
)

val TOP_LEVEL_NAV_ITEMS = mapOf(
    HomeNavKey to HOME,
    BrowseNavKey to BROWSE,
    CreatePostNavKey to POST,
    LeaderboardNavKey to LEADERBOARD,
    ProfileNavKey to PROFILE,
)