package com.kith.feature.post.impl.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.metadata
import androidx.navigation3.ui.NavDisplay
import com.kith.core.navigation.Navigator
import com.kith.feature.post.api.navigation.CreatePostNavKey
import com.kith.feature.post.api.navigation.PostDetailNavKey
import com.kith.feature.post.impl.CreatePostScreen
import com.kith.feature.post.impl.PostDetailScreen
import com.kith.feature.post.impl.PostDetailViewModel
import com.kith.feature.post.impl.PostDetailViewModel.Factory

fun EntryProviderScope<NavKey>.postEntry(navigator: Navigator) {
    entry<CreatePostNavKey>(
        metadata = metadata {
            put(NavDisplay.TransitionKey) {
                slideInVertically(initialOffsetY = { it }) togetherWith fadeOut()
            }
            put(NavDisplay.PopTransitionKey) {
                fadeIn() togetherWith slideOutVertically(targetOffsetY = { it })
            }
            put(NavDisplay.PredictivePopTransitionKey) {
                fadeIn() togetherWith slideOutVertically(targetOffsetY = { it })
            }
        }
    ) {
        CreatePostScreen(
            onNavigateBack = {}
        )
    }

    entry<PostDetailNavKey>(
        metadata = metadata {
            put(NavDisplay.TransitionKey) {
                slideInHorizontally(initialOffsetX = { it }) togetherWith fadeOut()
            }
            put(NavDisplay.PopTransitionKey) {
                fadeIn() togetherWith slideOutHorizontally(targetOffsetX = { it })
            }
            put(NavDisplay.PredictivePopTransitionKey) {
                fadeIn() togetherWith slideOutHorizontally(targetOffsetX = { it })
            }
        }
    ) { key ->
        val id = key.id
        PostDetailScreen(
            viewModel = hiltViewModel<PostDetailViewModel, Factory>(
                key = id,
            ) { factory ->
                factory.create(id)
            },
            onBackClick = { navigator.goBack() }
        )
    }
}