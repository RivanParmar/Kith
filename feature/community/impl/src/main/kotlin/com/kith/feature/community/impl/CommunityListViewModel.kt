package com.kith.feature.community.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kith.core.data.repository.CommunityRepository
import com.kith.core.model.data.Community
import com.kith.core.network.KithAuthDataSource
import com.kith.core.network.KithNetworkDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityListViewModel @Inject constructor(
    communityRepository: CommunityRepository,
    private val authDataSource: KithAuthDataSource,
    private val networkDataSource: KithNetworkDataSource
) : ViewModel() {

    private val isPremiumUser = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            authDataSource.currentUserId()?.let { userId ->

                // 1. Try to fetch premium status (If it fails, it's fine, default is false)
                try {
                    val user = networkDataSource.getUserById(userId)
                    isPremiumUser.value = user.isPremium
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                // 2. INDEPENDENTLY sync communities (so this runs even if the user fetch fails!)
                try {
                    communityRepository.syncJoinedCommunities()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    val uiState: StateFlow<CommunityListUiState> = combine(
        communityRepository.getJoinedCommunitiesStream(),
        isPremiumUser
    ) { communities, isPremium ->
        // Enforce the rule: Only Premium users get multiple joined communities in this view
        val displayList = if (isPremium) communities else communities.take(1)
        CommunityListUiState.Success(displayList, isPremium)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CommunityListUiState.Loading
    )
}

sealed interface CommunityListUiState {
    data object Loading : CommunityListUiState
    data class Success(
        val communities: List<Community>,
        val isPremium: Boolean,
    ) : CommunityListUiState
}