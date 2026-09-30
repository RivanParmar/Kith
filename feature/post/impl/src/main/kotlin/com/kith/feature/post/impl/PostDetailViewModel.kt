package com.kith.feature.post.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kith.core.data.repository.AuthRepository
import com.kith.core.data.repository.MediaRepository
import com.kith.core.data.repository.PostRepository
import com.kith.core.model.data.Community
import com.kith.core.model.data.PostDetail
import com.kith.core.model.data.User
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.Int
import kotlin.time.Instant

private data class PostTransientState(
    val isAcceptedByCurrentUser: Boolean = false,
    val solutionStatus: SolutionStatus? = null,
    val userRating: Float = 5f,
    val isSubmitting: Boolean = false,
    val isDeleting: Boolean = false
)

@HiltViewModel(assistedFactory = PostDetailViewModel.Factory::class)
class PostDetailViewModel @AssistedInject constructor(
    private val postRepository: PostRepository,
    private val authRepository: AuthRepository,
    @Assisted val postId: String,
    val mediaRepository: MediaRepository,
) : ViewModel() {

    private val transientState = MutableStateFlow(PostTransientState())

    val uiState: StateFlow<PostDetailUiState> = postDetailUiState(
        postId = postId,
        postRepository = postRepository,
        authRepository = authRepository,
        mediaRepository = mediaRepository,
        transientStateFlow = transientState
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PostDetailUiState.Loading,
    )

    init {
        viewModelScope.launch {
            try {
                postRepository.syncPostById(postId)
            } catch (_: Exception) { }
        }
    }

    fun onAcceptClick() {
        transientState.update { it.copy(isAcceptedByCurrentUser = !it.isAcceptedByCurrentUser) }
    }

    fun onSubmitAnswer(answer: String) {
        viewModelScope.launch {
            transientState.update { it.copy(isSubmitting = true) }
            try {
                val solverId = authRepository.currentUserId() ?: return@launch
                postRepository.submitAnswer(postId, answer, solverId)
                transientState.update { it.copy(isSubmitting = false, isAcceptedByCurrentUser = false) }
            } catch (_: Exception) { // FIX: Changed e to _ to clear warning
                transientState.update { it.copy(isSubmitting = false) }
            }
        }
    }

    fun onRateSolution(rating: Float) {
        viewModelScope.launch {
            try {
                postRepository.rateSolution(postId, rating)
                transientState.update { it.copy(userRating = rating) }
            } catch (_: Exception) {}
        }
    }

    fun onAcceptSolution() {
        transientState.update { it.copy(solutionStatus = SolutionStatus.ACCEPTED) }
        viewModelScope.launch {
            try {
                postRepository.acceptSolution(postId)
            } catch (_: Exception) {}
        }
    }

    fun onRejectSolution() {
        transientState.update { it.copy(solutionStatus = SolutionStatus.REJECTED) }
        viewModelScope.launch {
            try {
                postRepository.rejectSolution(postId)
            } catch (_: Exception) {}
        }
    }

    fun onDeletePost(onDeleted: () -> Unit) {
        viewModelScope.launch {
            transientState.update { it.copy(isDeleting = true) }
            try {
                postRepository.deletePost(postId)
                onDeleted()
            } catch (_: Exception) { // FIX: Changed e to _ to clear warning
                transientState.update { it.copy(isDeleting = false) }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(postId: String): PostDetailViewModel
    }
}

private fun postDetailUiState(
    postId: String,
    postRepository: PostRepository,
    authRepository: AuthRepository,
    mediaRepository: MediaRepository,
    transientStateFlow: StateFlow<PostTransientState>
): Flow<PostDetailUiState> {

    val currentUserIdStream = flow { emit(authRepository.currentUserId()) }
    val postStream = postRepository.getPostDetailStream(postId)

    return combine(
        postStream,
        currentUserIdStream,
        transientStateFlow
    ) { postDetail, currentUserId, transient ->

        val isAuthor = currentUserId != null && currentUserId == postDetail.author.id
        val initialStatus = if (postDetail.isAccepted) SolutionStatus.ACCEPTED else SolutionStatus.PENDING
        val effectiveStatus = transient.solutionStatus ?: initialStatus

        val resolvedUris = (0 until postDetail.userImageCount).mapNotNull { index ->
            mediaRepository.getResolvedMediaUri(
                postId = postId,
                role = "user",
                index = index,
                extension = "webp",
                isSolved = postDetail.isAccepted
            )
        }

        PostDetailUiState.Success(
            post = postDetail,
            resolvedImageUris = resolvedUris,
            isAuthor = isAuthor,
            isAcceptedByCurrentUser = transient.isAcceptedByCurrentUser,
            solutionStatus = initialStatus,
            // FIX: Prioritize the saved database rating over the transient default!
            userRating = postDetail.rating ?: transient.userRating,
            isSubmitting = transient.isSubmitting,
            isDeleting = transient.isDeleting
        ) as PostDetailUiState
    }.catch { exception ->
        emit(PostDetailUiState.Error(exception.message))
    }
}

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
    solver = null,
    rating = null,
    userImageCount = 1233,
    userHasPdf = false,
    userHasAudio = false,
    solverImageCount = 7,
    solverHasPdf = false,
    solverHasAudio = false,
)

val samplePostDetailAnswered = samplePostDetail.copy(
    answer = "A dialog is a type of modal window that appears in front of app content to provide critical information, or prompt for a decision to be made.",
    isAccepted = true
)