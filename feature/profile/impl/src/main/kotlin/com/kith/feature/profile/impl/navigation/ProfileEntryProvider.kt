package com.kith.feature.profile.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.kith.core.navigation.Navigator
import com.kith.feature.profile.api.navigation.PostHistoryNavKey
import com.kith.feature.profile.api.navigation.ProfileNavKey
import com.kith.feature.profile.api.navigation.TransactionsNavKey
import com.kith.feature.profile.impl.PostHistoryScreen
import com.kith.feature.profile.impl.ProfileScreen
import com.kith.feature.profile.impl.TransactionsScreen

fun EntryProviderScope<NavKey>.profileEntry(navigator: Navigator) {
    entry<ProfileNavKey> {
        ProfileScreen(
            onWalletClick = { navigator.navigate(TransactionsNavKey) },
            onPostHistoryClick = { navigator.navigate(PostHistoryNavKey) }
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
}