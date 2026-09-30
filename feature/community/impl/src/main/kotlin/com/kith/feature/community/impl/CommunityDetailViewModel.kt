package com.kith.feature.community.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kith.core.data.repository.CommunityRepository
import com.kith.core.data.repository.MediaRepository
import com.kith.core.model.data.Community
import com.kith.core.network.KithAuthDataSource
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = CommunityDetailViewModel.Factory::class)
class CommunityDetailViewModel @AssistedInject constructor(
    @Assisted val communityId: String,
    private val communityRepository: CommunityRepository,
    private val mediaRepository: MediaRepository,
    authDataSource: KithAuthDataSource
) : ViewModel() {

    private val currentUserId = authDataSource.currentUserId()

    val uiState: StateFlow<CommunityDetailUiState> = communityRepository.getCommunityByIdStream(communityId)
        .map { community ->
            val isAdmin = currentUserId != null && community.creatorId == currentUserId
            CommunityDetailUiState.Success(community, isAdmin)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CommunityDetailUiState.Loading
        )

    fun leaveOrDeleteCommunity(isAdmin: Boolean, onComplete: () -> Unit) {
        viewModelScope.launch {
            if (isAdmin) {
                communityRepository.deleteCommunity(communityId)
            } else {
                communityRepository.leaveCommunity(communityId)
            }
            onComplete()
        }
    }

    fun updateDescription(description: String) {
        viewModelScope.launch {
            communityRepository.updateCommunityDescription(communityId, description)
        }
    }

    fun updateCommunityImage(imageUri: String) {
        viewModelScope.launch {
            val uploadResult = mediaRepository.uploadCommunityImage(communityId, imageUri)
            uploadResult.onSuccess { url ->
                // Ensure your CommunityRepository accepts the imageUrl update
                communityRepository.updateCommunityImage(communityId, url)
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(communityId: String): CommunityDetailViewModel
    }
}

sealed interface CommunityDetailUiState {
    data object Loading : CommunityDetailUiState
    data class Success(val community: Community, val isAdmin: Boolean) : CommunityDetailUiState
}