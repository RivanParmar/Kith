package com.kith.feature.community.impl

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kith.core.data.repository.CommunityRepository
import com.kith.core.model.data.Community
import com.kith.core.network.KithAuthDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val communityRepository: CommunityRepository,
    authDataSource: KithAuthDataSource
) : ViewModel() {

    // Expects communityId to be passed via NavKey
    private val communityId: String = checkNotNull(savedStateHandle["communityId"])
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
}

sealed interface CommunityDetailUiState {
    data object Loading : CommunityDetailUiState
    data class Success(val community: Community, val isAdmin: Boolean) : CommunityDetailUiState
}

