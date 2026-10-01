package com.kith.feature.home.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.kith.core.navigation.Navigator
import com.kith.core.navigation.horizontalSlideMetadata
import com.kith.feature.home.api.navigation.HomeNavKey
import com.kith.feature.home.api.navigation.NotificationsNavKey
import com.kith.feature.home.impl.HomeRoute
import com.kith.feature.home.impl.ui.notifications.NotificationsRoute
import com.kith.feature.post.api.navigation.navigateToPostDetail
import com.kith.feature.profile.api.navigation.ProfileNavKey
import com.kith.feature.profile.api.navigation.UserProfileNavKey

fun EntryProviderScope<NavKey>.homeEntry(navigator: Navigator) {
    entry<HomeNavKey> {
        HomeRoute(
            onPostClick = { navigator.navigateToPostDetail(it) },
            onAuthorClick = { userId -> navigator.navigate(UserProfileNavKey(userId)) },
            onNavigateToMyProfile = { navigator.navigate(ProfileNavKey) },
            onNavigateToNotifications = { navigator.navigate(NotificationsNavKey) }
        )
    }

    entry<NotificationsNavKey>(
        metadata = horizontalSlideMetadata()
    ) {
        NotificationsRoute(
            onNavigateToPost = { navigator.navigateToPostDetail(it) },
            onBackClick = { navigator.goBack() }
        )
    }
}