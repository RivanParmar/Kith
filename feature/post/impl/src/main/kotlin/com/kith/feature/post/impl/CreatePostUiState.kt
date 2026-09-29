package com.kith.feature.post.impl

import androidx.compose.runtime.Immutable
import com.kith.core.model.data.Community
import com.kith.core.model.data.User

enum class PostReward(val title: String, val rewardValue: Int) {
    EASY("Easy", 20),
    MEDIUM("Medium", 35),
    HARD("Hard", 50),
}

@Immutable
data class CreatePostFormState(
    val title: String = "",
    val content: String = "",
    val selectedImageUris: List<String> = emptyList(),
    val selectedPdfUri: String? = null,
    val selectedAudioUri: String? = null,
    val reward: PostReward = PostReward.EASY,
    val selectedCommunity: Community? = null, // Changed from String
    val isInPerson: Boolean = false,
    val selectedTargetUser: User? = null, // Changed from String
) {
    val canAddMoreImages: Boolean get() = selectedImageUris.size < 5
    val isValid: Boolean
        get() = title.isNotBlank() && content.isNotBlank() && selectedCommunity != null // Changed to null check
}

sealed interface CommunitiesUiState {
    data object Loading : CommunitiesUiState
    data class Success(val communities: List<Community>) : CommunitiesUiState
    data object Error : CommunitiesUiState
}

sealed interface PostSubmissionState {
    data object Idle : PostSubmissionState
    data object Submitting : PostSubmissionState
    data object Success : PostSubmissionState
    data class Error(val message: String) : PostSubmissionState
}