package com.kith.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kith.core.designsystem.component.LoadingWheel
import com.kith.core.model.data.Post

fun LazyGridScope.postsFeed(
    feedState: PostsFeedUiState,
) {
    when (feedState) {
        PostsFeedUiState.Loading -> {
            item {
                LoadingState()
            }
        }
        is PostsFeedUiState.Success -> {
            items(
                items = feedState.feed,
                key = { it.id },
                contentType = { "postsFeedItem" },
            ) { post ->
                PostCard(
                    post = post,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .animateItem(),
                )
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize()) {
        LoadingWheel(
            contentDesc = "Loading posts",
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

sealed interface PostsFeedUiState {
    data object Loading : PostsFeedUiState

    data class Success(
        val feed: List<Post>,
    ) : PostsFeedUiState
}