package com.kith.app.features.joincommunity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kith.feature.community.impl.JoinCommunityUiState
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * One-off navigation/UI events that shouldn't live inside the state itself
 * (things like "go back" or "open create community screen" should only fire once).
 */
sealed interface JoinCommunityEvent {
    data object NavigateBack : JoinCommunityEvent
    data object NavigateToCreateCommunity : JoinCommunityEvent
    data object NavigateToFindCommunity : JoinCommunityEvent
    data object NavigateToHome : JoinCommunityEvent
}

@HiltViewModel
class JoinCommunityViewModel @Inject constructor(
    private val communityRepository: CommunityRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(JoinCommunityUiState())
    val uiState: StateFlow<JoinCommunityUiState> = _uiState.asStateFlow()

    private val _events = Channel<JoinCommunityEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onCommunityNameChanged(value: String) {
        _uiState.value = _uiState.value.copy(communityName = value, errorMessage = null)
    }

    fun onPasswordChanged(value: String) {
        _uiState.value = _uiState.value.copy(communityPassword = value, errorMessage = null)
    }

    fun onBackClicked() {
        viewModelScope.launch { _events.send(JoinCommunityEvent.NavigateBack) }
    }

    fun onCantFindCommunityClicked() {
        viewModelScope.launch { _events.send(JoinCommunityEvent.NavigateToFindCommunity) }
    }

    fun onCreateCommunityClicked() {
        viewModelScope.launch { _events.send(JoinCommunityEvent.NavigateToCreateCommunity) }
    }

    fun onJoinClicked() {
        val state = _uiState.value
        if (!state.isJoinEnabled) return

        viewModelScope.launch {
            _uiState.value = state.copy(isJoining = true, errorMessage = null)
            val result = communityRepository.joinCommunity(
                name = state.communityName.trim(),
                password = state.communityPassword
            )
            result.onSuccess {
                _uiState.value = _uiState.value.copy(isJoining = false, joinSuccessful = true)
                _events.send(JoinCommunityEvent.NavigateToHome)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isJoining = false,
                    errorMessage = error.message ?: "Couldn't join that community. Try again."
                )
            }
        }
    }
}

/**
 * Placeholder repository — replace this with your actual backend/Firebase/Ktor call.
 */
class CommunityRepository {
    suspend fun joinCommunity(name: String, password: String): Result<Unit> {
        // TODO: hook this up to your real data source
        return if (name.isNotBlank() && password.isNotBlank()) {
            Result.success(Unit)
        } else {
            Result.failure(IllegalArgumentException("Community name or password is incorrect"))
        }
    }
}