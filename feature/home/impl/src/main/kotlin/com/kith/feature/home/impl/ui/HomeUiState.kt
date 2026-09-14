package com.kith.feature.home.impl.ui

import com.kith.core.model.data.Post

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Success(
        val currentBalance: Int,
        val currentLevel: Int,
        val nextTierXp: Int,
        val recentPosts: List<Post>
    ) : HomeUiState

    data class Error(val message: String) : HomeUiState
}