package com.kith.app.features.createcommunity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kith.feature.community.impl.CreateCommunityUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * One-off navigation/UI events that shouldn't live inside the state itself.
 */
sealed interface CreateCommunityEvent {
    data object NavigateBack : CreateCommunityEvent
    data object NavigateToHome : CreateCommunityEvent
    data object OpenIconPicker : CreateCommunityEvent
}

@HiltViewModel
class CreateCommunityViewModel @Inject constructor(
    private val communityRepository: CommunityCreationRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateCommunityUiState())
    val uiState: StateFlow<CreateCommunityUiState> = _uiState.asStateFlow()

    private val _events = Channel<CreateCommunityEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onCommunityNameChanged(value: String) {
        _uiState.value = _uiState.value.copy(communityName = value, errorMessage = null)
    }

    fun onPasswordChanged(value: String) {
        _uiState.value = _uiState.value.copy(communityPassword = value, errorMessage = null)
    }

    fun onDescriptionChanged(value: String) {
        _uiState.value = _uiState.value.copy(communityDescription = value)
    }

    fun onBackClicked() {
        viewModelScope.launch { _events.send(CreateCommunityEvent.NavigateBack) }
    }

    fun onAddIconClicked() {
        viewModelScope.launch { _events.send(CreateCommunityEvent.OpenIconPicker) }
    }

    fun onCreateClicked() {
        val state = _uiState.value
        if (!state.isCreateEnabled) return

        viewModelScope.launch {
            _uiState.value = state.copy(isCreating = true, errorMessage = null)
            val result = communityRepository.createCommunity(
                name = state.communityName.trim(),
                password = state.communityPassword,
                description = state.communityDescription.trim()
            )
            result.onSuccess {
                _uiState.value = _uiState.value.copy(isCreating = false, createSuccessful = true)
                _events.send(CreateCommunityEvent.NavigateToHome)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isCreating = false,
                    errorMessage = error.message ?: "Couldn't create that community. Try again."
                )
            }
        }
    }
}

/**
 * Placeholder repository — replace this with your actual backend/Firebase/Ktor call.
 */
class CommunityCreationRepository {
    suspend fun createCommunity(name: String, password: String, description: String): Result<Unit> {
        // TODO: hook this up to your real data source
        return if (name.isNotBlank() && password.isNotBlank()) {
            Result.success(Unit)
        } else {
            Result.failure(IllegalArgumentException("Community name or password is missing"))
        }
    }
}