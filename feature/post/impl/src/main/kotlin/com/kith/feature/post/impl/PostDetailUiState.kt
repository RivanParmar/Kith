package com.kith.feature.post.impl

import com.kith.core.model.data.PostDetail

enum class SolutionStatus {
    PENDING,
    ACCEPTED,
    REJECTED
}

sealed interface PostDetailUiState {
    data object Loading : PostDetailUiState
    data class Error(val message: String?) : PostDetailUiState
    data class Success(
        val post: PostDetail,
        val authorImageUris: List<String> = emptyList(),
        val authorPdfUri: String? = null,
        val authorAudioUri: String? = null,
        val solverImageUris: List<String> = emptyList(),
        val solverPdfUri: String? = null,
        val solverAudioUri: String? = null,
        val isAuthor: Boolean,
        val isAcceptedByCurrentUser: Boolean,
        val solutionStatus: SolutionStatus,
        val userRating: Float,
        val isSubmitting: Boolean,
        val isDeleting: Boolean,
    ) : PostDetailUiState
}