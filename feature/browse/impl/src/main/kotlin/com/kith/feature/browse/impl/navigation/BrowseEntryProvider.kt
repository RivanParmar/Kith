package com.kith.feature.browse.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.kith.core.navigation.Navigator
import com.kith.feature.browse.api.navigation.BrowseNavKey
import com.kith.feature.browse.impl.BrowseScreen
import com.kith.feature.post.api.navigation.navigateToPostDetail
import com.kith.feature.profile.api.navigation.ProfileNavKey
import com.kith.feature.profile.api.navigation.UserProfileNavKey

fun EntryProviderScope<NavKey>.browseEntry(navigator: Navigator) {
    entry<BrowseNavKey> {
        BrowseScreen(
            onPostClick = { navigator.navigateToPostDetail(it) },
            onAuthorClick = { userId -> navigator.navigate(UserProfileNavKey(userId)) },
            onNavigateToMyProfile = { navigator.navigate(ProfileNavKey) }
        )
    }
}