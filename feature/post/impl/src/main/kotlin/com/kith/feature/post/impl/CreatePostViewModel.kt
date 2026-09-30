package com.kith.feature.post.impl

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kith.core.data.repository.CommunityRepository
import com.kith.core.data.repository.UserRepository
import com.kith.core.data.repository.WalletRepository // ADDED
import com.kith.core.domain.CreatePostUseCase
import com.kith.core.model.data.NewPostRequest
import com.kith.core.model.data.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val createPostUseCase: CreatePostUseCase,
    private val communityRepository: CommunityRepository,
    private val walletRepository: WalletRepository, // ADDED
    userRepository: UserRepository,
) : ViewModel() {

    private val _formState = MutableStateFlow(CreatePostFormState())
    val formState: StateFlow<CreatePostFormState> = _formState.asStateFlow()

    private val _submissionState = MutableStateFlow<PostSubmissionState>(PostSubmissionState.Idle)
    val submissionState: StateFlow<PostSubmissionState> = _submissionState.asStateFlow()

    private val _userSearchResults = MutableStateFlow<List<User>>(emptyList())
    val userSearchResults: StateFlow<List<User>> = _userSearchResults.asStateFlow()

    val communitiesUiState: StateFlow<CommunitiesUiState> =
        // FIX: Replaced getAvailableCommunities with getJoinedCommunitiesStream
        communityRepository.getJoinedCommunitiesStream()
            .map { CommunitiesUiState.Success(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = CommunitiesUiState.Loading,
            )

    val isPremium: StateFlow<Boolean> = userRepository.getUserProfileStream()
        .map { profile -> profile?.isPremium == true }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    fun dismissSubmissionError() {
        _submissionState.value = PostSubmissionState.Idle
    }

    fun searchUsersInCommunity(communityId: String?, query: String) {
        if (query.isBlank() || communityId == null) {
            _userSearchResults.value = emptyList()
            return
        }

        viewModelScope.launch {
            communityRepository.searchCommunityMembers(communityId, query)
                .onSuccess { users ->
                    _userSearchResults.value = users
                }
                .onFailure { error ->
                    _userSearchResults.value = emptyList()
                    Log.e("CREATE_POST", "Failed to fetch users", error)
                }
        }
    }

    fun clearUserSearch() {
        _userSearchResults.value = emptyList()
    }

    fun createPost(currentForm: CreatePostFormState) {
        if (!currentForm.isValid) {
            _submissionState.value = PostSubmissionState.Error("Please fill in the title, description, and select a community.")
            return
        }

        viewModelScope.launch {
            _submissionState.value = PostSubmissionState.Submitting

            try {
                // FIX: Check user XP balance before allowing creation
                val walletData = walletRepository.getWalletDataStream().firstOrNull()
                val currentXp = walletData?.balance ?: 0
                val requiredXp = currentForm.reward.rewardValue

                if (currentXp < requiredXp) {
                    _submissionState.value = PostSubmissionState.Error("Not enough XP. You need $requiredXp XP to post this task.")
                    return@launch
                }

                val communityId = currentForm.selectedCommunity?.id
                    ?: throw IllegalStateException("Community not selected")

                val solverId = currentForm.selectedTargetUser?.id

                val postStatus = when {
                    currentForm.selectedTargetUser != null -> "ASSIGNED"
                    else -> "OPEN"
                }

                val request = NewPostRequest(
                    title = currentForm.title,
                    content = currentForm.content,
                    communityId = communityId,
                    reward = requiredXp,
                    isInPerson = currentForm.isInPerson,
                    imageUris = currentForm.selectedImageUris,
                    pdfUri = currentForm.selectedPdfUri,
                    audioUri = currentForm.selectedAudioUri,
                    status = postStatus,
                    solverId = solverId
                )

                createPostUseCase(request)

                _submissionState.value = PostSubmissionState.Success
            } catch (e: Exception) {
                _submissionState.value = PostSubmissionState.Error(e.message ?: "Failed to create post")
            }
        }
    }
}