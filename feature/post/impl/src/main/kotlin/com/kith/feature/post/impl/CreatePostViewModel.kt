package com.kith.feature.post.impl

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kith.core.data.repository.CommunityRepository
import com.kith.core.domain.CreatePostUseCase
import com.kith.core.model.data.NewPostRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

//@HiltViewModel
//class CreatePostViewModel @Inject constructor(
//    private val createPostUseCase: CreatePostUseCase,
//) : ViewModel() {
//
//    fun submitPost(title: String, content: String) {
//        viewModelScope.launch {
//            try {
//                val request = NewPostRequest(
//                    title = title,
//                    content = content,
//                    communityId = "7dcf257c-977d-4252-a18e-d7178774aabe",
//                    reward = 20,
//                    isInPerson = false,
//                    imageUris = emptyList(),
//                    pdfUri = null,
//                    audioUri = null,
//                )
//
//                createPostUseCase(request)
//                Log.d("CREATE", "Success!")
//            } catch (e: Exception) {
//                Log.d("CREATE", "Failed!")
//            }
//        }
//    }
//}
@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val createPostUseCase: CreatePostUseCase,
    communityRepository: CommunityRepository,
) : ViewModel() {

    private val _formState = MutableStateFlow(CreatePostFormState())
    val formState: StateFlow<CreatePostFormState> = _formState.asStateFlow()

    private val _submissionState = MutableStateFlow<PostSubmissionState>(PostSubmissionState.Idle)
    val submissionState: StateFlow<PostSubmissionState> = _submissionState.asStateFlow()

    val communitiesUiState: StateFlow<CommunitiesUiState> =
        communityRepository.getAvailableCommunities()
            .map { CommunitiesUiState.Success(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = CommunitiesUiState.Loading,
            )

    fun dismissSubmissionError() {
        _submissionState.value = PostSubmissionState.Idle
    }

    fun createPost(isDraft: Boolean, currentForm: CreatePostFormState) {
        if (!currentForm.isValid) {
            // Tell the UI to show an error instead of failing silently
            _submissionState.value = PostSubmissionState.Error("Please fill in the title, description, and select a community.")
            Log.d("CREATE", "Reached here!")
            return
        }

        viewModelScope.launch {
            _submissionState.value = PostSubmissionState.Submitting

            try {
                val currentState = communitiesUiState.value
                val communityId = if (currentState is CommunitiesUiState.Success) {
                    currentState.communities.find { it.name == currentForm.selectedCommunity }!!.id
                } else {
                    throw IllegalStateException("Communities not loaded")
                }

                val request = NewPostRequest(
                    title = currentForm.title,
                    content = currentForm.content,
                    communityId = communityId,
                    reward = currentForm.reward.rewardValue,
                    isInPerson = currentForm.isInPerson,
                    imageUris = currentForm.selectedImageUris,
                    pdfUri = currentForm.selectedPdfUri,
                    audioUri = currentForm.selectedAudioUri,
                    isDraft = isDraft,
                )

                createPostUseCase(request)

                _submissionState.value = PostSubmissionState.Success
            } catch (e: Exception) {
                _submissionState.value = PostSubmissionState.Error(e.message.toString())
            }
        }
    }
}