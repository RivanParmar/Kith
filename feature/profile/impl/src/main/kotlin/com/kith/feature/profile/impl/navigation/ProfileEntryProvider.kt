package com.kith.feature.profile.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.kith.core.navigation.Navigator
import com.kith.feature.community.api.navigation.CommunityListNavKey
import com.kith.feature.profile.api.navigation.PostHistoryNavKey
import com.kith.feature.profile.api.navigation.ProfileNavKey
import com.kith.feature.profile.api.navigation.TransactionsNavKey
import com.kith.feature.profile.api.navigation.UserProfileNavKey
import com.kith.feature.profile.impl.OtherUserProfileScreen
import com.kith.feature.profile.impl.PostHistoryScreen
import com.kith.feature.profile.impl.ProfileScreen
import com.kith.feature.profile.impl.TransactionsScreen

fun EntryProviderScope<NavKey>.profileEntry(navigator: Navigator) {
    entry<ProfileNavKey> {
        ProfileScreen(
            onWalletClick = { navigator.navigate(TransactionsNavKey) },
            onPostHistoryClick = { navigator.navigate(PostHistoryNavKey) },
            onNavigateToCommunity = { navigator.navigate(CommunityListNavKey) },
        )
    }

    entry<TransactionsNavKey> {
        TransactionsScreen(
            onBackClick = navigator::goBack
        )
    }

    entry<PostHistoryNavKey> {
        PostHistoryScreen()
    }

    entry<UserProfileNavKey> { key ->
        OtherUserProfileScreen(
            userId = key.userId,
            onBackClick = { navigator.goBack() }
        )
    }
}