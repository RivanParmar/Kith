package com.kith.feature.post.impl

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kith.core.data.repository.AuthRepository
import com.kith.core.data.repository.PostRepository
import com.kith.core.model.data.Community
import com.kith.core.model.data.PostDetail
import com.kith.core.model.data.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Instant

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val postRepository: PostRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PostDetailUiState>(PostDetailUiState.Loading)
    val uiState: StateFlow<PostDetailUiState> = _uiState.asStateFlow()

    private val postId: String? = savedStateHandle["postId"]

    init {
        if (postId != null) {
            loadPostDetail(postId)
        } else {
            _uiState.value = PostDetailUiState.Error("Post ID is missing")
        }
    }

    private fun loadPostDetail(id: String) {
        viewModelScope.launch {
            val currentUserId = authRepository.currentUserId()

            postRepository.getPostDetailStream(id).collect { postDetail ->
                val isAuthor = currentUserId != null && currentUserId == postDetail.author.id
                val initialStatus = if (postDetail.isAccepted) SolutionStatus.ACCEPTED else SolutionStatus.PENDING

                _uiState.update { currentState ->
                    if (currentState is PostDetailUiState.Success) {
                        currentState.copy(
                            post = postDetail,
                            isAuthor = isAuthor,
                            solutionStatus = initialStatus
                        )
                    } else {
                        PostDetailUiState.Success(
                            post = postDetail,
                            isAuthor = isAuthor,
                            solutionStatus = initialStatus
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
            try {
                postRepository.syncPostById(id)
            } catch (_: Exception) {
                // Background refresh fallback
            }
        }
    }

    fun onAcceptClick() {
        _uiState.update { currentState ->
            if (currentState is PostDetailUiState.Success) {
                currentState.copy(isAcceptedByCurrentUser = !currentState.isAcceptedByCurrentUser)
            } else {
                currentState
            }
        }
    }

    fun onSubmitAnswer(answer: String) {
        val id = postId ?: return
        viewModelScope.launch {
            _uiState.update { if (it is PostDetailUiState.Success) it.copy(isSubmitting = true) else it }
            try {
                postRepository.submitAnswer(id, answer)
                _uiState.update { currentState ->
                    if (currentState is PostDetailUiState.Success) {
                        currentState.copy(isSubmitting = false, isAcceptedByCurrentUser = false)
                    } else currentState
                }
            } catch (e: Exception) {
                _uiState.update { if (it is PostDetailUiState.Success) it.copy(isSubmitting = false) else it }
            }
        }
    }

    fun onRateSolution(rating: Int) {
        val id = postId ?: return
        _uiState.update { if (it is PostDetailUiState.Success) it.copy(userRating = rating) else it }
        viewModelScope.launch {
            try {
                postRepository.rateSolution(id, rating)
            } catch (_: Exception) {}
        }
    }

    fun onAcceptSolution() {
        val id = postId ?: return
        viewModelScope.launch {
            try {
                postRepository.acceptSolution(id)
                _uiState.update { if (it is PostDetailUiState.Success) it.copy(solutionStatus = SolutionStatus.ACCEPTED) else it }
            } catch (_: Exception) {}
        }
    }

    fun onRejectSolution() {
        val id = postId ?: return
        viewModelScope.launch {
            try {
                postRepository.rejectSolution(id)
                _uiState.update { if (it is PostDetailUiState.Success) it.copy(solutionStatus = SolutionStatus.REJECTED) else it }
            } catch (_: Exception) {}
        }
    }

    fun onDeletePost(onDeleted: () -> Unit) {
        val id = postId ?: return
        viewModelScope.launch {
            _uiState.update { if (it is PostDetailUiState.Success) it.copy(isDeleting = true) else it }
            try {
                postRepository.deletePost(id)
                onDeleted()
            } catch (e: Exception) {
                _uiState.update { if (it is PostDetailUiState.Success) it.copy(isDeleting = false) else it }
            }
        }
    }
}

// =============================================================================
// SAMPLE DATA FOR PREVIEWS
// =============================================================================

val samplePostAuthor = User(
    id = "user-101",
    name = "Elena Rostova",
    profileImageUrl = null,
    isPremium = true,
    rating = 4.9f
)

val samplePostCommunity = Community(
    id = "comm-202",
    name = "Community-Name",
    imageUrl = null,
    description = null,
    creatorId = "",
)

val samplePostDetail = PostDetail(
    id = "post-303",
    title = "Help editing Econ 310 Research Paper draft",
    content = "Need a second pair of eyes to proofread and correct reference style citations on my Econ research proposal draft. It is about 5 pages. I will be sitting at the library desk until 4 PM!",
    reward = 350,
    author = samplePostAuthor,
    community = samplePostCommunity,
    createdAt = Instant.fromEpochMilliseconds(System.currentTimeMillis()),
    isInPerson = false,
    isAccepted = false,
    answer = null,
    solver = null
)

val samplePostDetailAnswered = samplePostDetail.copy(
    answer = "A dialog is a type of modal window that appears in front of app content to provide critical information, or prompt for a decision to be made.",
    isAccepted = true
)