package com.kith.feature.post.impl

import com.kith.core.model.data.PostDetail

enum class SolutionStatus {
    PENDING,
    ACCEPTED,
    REJECTED
}

sealed interface PostDetailUiState {
    data object Loading : PostDetailUiState

    data class Success(
        val post: PostDetail,
        val isAuthor: Boolean = false,
        val isAcceptedByCurrentUser: Boolean = false,
        val solutionStatus: SolutionStatus = SolutionStatus.PENDING,
        val userRating: Int = 0, // Defaults to 0 so the stars are empty initially
        val isSubmitting: Boolean = false,
        val isDeleting: Boolean = false
    ) : PostDetailUiState

    data class Error(val message: String? = null) : PostDetailUiState
}