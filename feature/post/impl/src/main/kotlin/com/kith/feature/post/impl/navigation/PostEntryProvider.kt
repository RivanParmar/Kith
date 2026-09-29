package com.kith.feature.post.impl.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.kith.core.navigation.Navigator
import com.kith.feature.post.api.navigation.CreatePostNavKey
import com.kith.feature.post.api.navigation.PostDetailNavKey
import com.kith.feature.post.impl.CreatePostScreen
import com.kith.feature.post.impl.PostDetailScreen
import com.kith.feature.post.impl.PostDetailViewModel
import com.kith.feature.post.impl.PostDetailViewModel.Factory

fun EntryProviderScope<NavKey>.postEntry(navigator: Navigator) {
    entry<CreatePostNavKey> {
        CreatePostScreen(
            onNavigateBack = {}
        )
    }

    entry<PostDetailNavKey> { key ->
        val id = key.id
        PostDetailScreen(
            viewModel = hiltViewModel<PostDetailViewModel, Factory>(
                key = id,
            ) { factory ->
                factory.create(id)
            },
        )
    }
}