package com.kith.feature.home.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.kith.core.navigation.Navigator
import com.kith.feature.home.api.navigation.HomeNavKey
import com.kith.feature.home.impl.HomeRoute
import com.kith.feature.post.api.navigation.navigateToPostDetail

fun EntryProviderScope<NavKey>.homeEntry(navigator: Navigator) {
    entry<HomeNavKey> {
        HomeRoute(
            onPostClick = { navigator.navigateToPostDetail(it) },
        )
    }
}