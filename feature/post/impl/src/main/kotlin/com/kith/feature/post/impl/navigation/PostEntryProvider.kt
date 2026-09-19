package com.kith.feature.post.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.kith.core.navigation.Navigator
import com.kith.feature.post.api.navigation.PostNavKey
import com.kith.feature.post.impl.PostDetailScreen

fun EntryProviderScope<NavKey>.postEntry(navigator: Navigator) {
    entry<PostNavKey> {
        PostDetailScreen()
    }
}