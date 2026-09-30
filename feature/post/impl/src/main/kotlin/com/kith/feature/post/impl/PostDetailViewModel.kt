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
    val userRating: Int = 5,
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

    // Manage only the transient UI state manually
    private val transientState = MutableStateFlow(PostTransientState())

    // 2. The NiA Reactive Pipeline
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
        // Trigger background network sync exactly like NiA does for topics
        viewModelScope.launch {
            try {
                postRepository.syncPostById(postId)
            } catch (_: Exception) {
                // Background refresh fallback
            }
        }
    }

    fun onAcceptClick() {
        transientState.update { it.copy(isAcceptedByCurrentUser = !it.isAcceptedByCurrentUser) }
    }

    fun onSubmitAnswer(answer: String) {
        viewModelScope.launch {
            transientState.update { it.copy(isSubmitting = true) }
            try {
                postRepository.submitAnswer(postId, answer, authRepository.currentUserId()!!)
                transientState.update { it.copy(isSubmitting = false, isAcceptedByCurrentUser = false) }
            } catch (e: Exception) {
                transientState.update { it.copy(isSubmitting = false) }
            }
        }
    }

    fun onRateSolution(rating: Int) {
        transientState.update { it.copy(userRating = rating) }
        viewModelScope.launch {
            try {
                postRepository.rateSolution(postId, rating)
            } catch (_: Exception) {}
        }
    }

    fun onAcceptSolution() {
        transientState.update { it.copy(solutionStatus = SolutionStatus.ACCEPTED) }
        viewModelScope.launch {
            try {
                postRepository.acceptSolution(postId)
            } catch (e: Exception) {
                android.util.Log.e("PostDetailViewModel", "Error in acceptSolution", e)
            }
        }
    }

    fun onRejectSolution() {
        transientState.update { it.copy(solutionStatus = SolutionStatus.REJECTED) }
        viewModelScope.launch {
            try {
                postRepository.rejectSolution(postId)
            } catch (e: Exception) {
                android.util.Log.e("PostDetailViewModel", "Error in rejectSolution", e)
            }
        }
    }

    fun onDeletePost(onDeleted: () -> Unit) {
        viewModelScope.launch {
            transientState.update { it.copy(isDeleting = true) }
            try {
                postRepository.deletePost(postId)
                onDeleted()
            } catch (e: Exception) {
                transientState.update { it.copy(isDeleting = false) }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(postId: String): PostDetailViewModel
    }
}

// 3. The pure flow-builder function (Mimics NiA's topicUiState)
private fun postDetailUiState(
    postId: String,
    postRepository: PostRepository,
    authRepository: AuthRepository,
    mediaRepository: MediaRepository,
    transientStateFlow: StateFlow<PostTransientState>
): Flow<PostDetailUiState> {

    val currentUserIdStream = flow { emit(authRepository.currentUserId()) }
    val postStream = postRepository.getPostDetailStream(postId)

    // Combine all 3 sources directly into your Success state
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
            solutionStatus = effectiveStatus,
            userRating = transient.userRating,
            isSubmitting = transient.isSubmitting,
            isDeleting = transient.isDeleting
        ) as PostDetailUiState
    }
        // Catch any database or auth crashes and map them to the Error state
        .catch { exception ->
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