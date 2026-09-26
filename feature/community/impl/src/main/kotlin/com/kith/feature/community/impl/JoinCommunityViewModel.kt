package com.kith.feature.community.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kith.core.data.repository.CommunityRepository
import com.kith.core.network.model.NetworkCommunity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.find

@HiltViewModel
class JoinCommunityViewModel @Inject constructor(
    private val communityRepository: CommunityRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(JoinCommunityUiState())
    val uiState: StateFlow<JoinCommunityUiState> = _uiState.asStateFlow()

    // Cached in memory for matching name -> id
    private var serverCommunities: List<NetworkCommunity> = emptyList()

    init {
        fetchCommunitiesFromServer()
    }

    private fun fetchCommunitiesFromServer() {
        viewModelScope.launch {
            communityRepository.getAvailableCommunities().collect { list ->
                serverCommunities = list
                _uiState.update { state ->
                    state.copy(
                        availableCommunities = list.map { it.name }
                    )
                }
            }
        }
    }

    fun onCommunityNameChanged(name: String) {
        _uiState.update { it.copy(communityName = name, errorMessage = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(communityPassword = password, errorMessage = null) }
    }

    fun onJoinClicked() {
        val currentState = _uiState.value
        if (currentState.isJoining) return

        // Resolve communityId from the selected community name
        val matchedCommunity = serverCommunities.find {
            it.name.equals(currentState.communityName.trim(), ignoreCase = true)
        }

        val communityId = matchedCommunity?.id ?: currentState.communityName.trim()

        viewModelScope.launch {
            _uiState.update { it.copy(isJoining = true, errorMessage = null) }

            val result = communityRepository.attemptJoinCommunity(
                communityId = communityId,
                password = currentState.communityPassword
            )

            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isJoining = false, joinSuccessful = true) }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isJoining = false,
                            errorMessage = error.localizedMessage ?: "Failed to join community"
                        )
                    }
                }
            )
        }
    }
}