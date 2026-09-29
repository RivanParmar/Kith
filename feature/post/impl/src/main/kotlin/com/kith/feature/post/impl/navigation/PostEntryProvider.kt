package com.kith.feature.post.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.kith.core.navigation.Navigator
import com.kith.feature.post.api.navigation.CreatePostNavKey
import com.kith.feature.post.api.navigation.PostDetailNavKey
import com.kith.feature.post.impl.CreatePostScreen
import com.kith.feature.post.impl.PostDetailScreen

fun EntryProviderScope<NavKey>.postEntry(navigator: Navigator) {
    entry<CreatePostNavKey> {
        CreatePostScreen(
            onNavigateBack = {}
        )
    }

    entry<PostDetailNavKey> {
        PostDetailScreen()
    }
}