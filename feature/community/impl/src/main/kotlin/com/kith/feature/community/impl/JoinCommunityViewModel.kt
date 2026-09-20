package com.kith.feature.community.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JoinCommunityViewModel @Inject constructor(

) : ViewModel() {

    private val _uiState = MutableStateFlow(JoinCommunityUiState())
    val uiState: StateFlow<JoinCommunityUiState> = _uiState.asStateFlow()

    fun onCommunityNameChanged(value: String) {
        _uiState.value = _uiState.value.copy(communityName = value, errorMessage = null)
    }

    fun onPasswordChanged(value: String) {
        _uiState.value = _uiState.value.copy(communityPassword = value, errorMessage = null)
    }

    fun onJoinClicked() {
        val state = _uiState.value
        if (!state.isJoinEnabled) return

        viewModelScope.launch {
            _uiState.value = state.copy(isJoining = true, errorMessage = null)
//            val result = communityRepository.joinCommunity(
//                name = state.communityName.trim(),
//                password = state.communityPassword
//            )
//            result.onSuccess {
//                _uiState.value = _uiState.value.copy(isJoining = false, joinSuccessful = true)
//                _events.send(JoinCommunityEvent.NavigateToHome)
//            }.onFailure { error ->
//                _uiState.value = _uiState.value.copy(
//                    isJoining = false,
//                    errorMessage = error.message ?: "Couldn't join that community. Try again."
//                )
//            }
        }
    }
}