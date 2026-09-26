package com.kith.feature.community.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kith.core.data.repository.CommunityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateCommunityViewModel @Inject constructor(
    private val communityRepository: CommunityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateCommunityUiState())
    val uiState: StateFlow<CreateCommunityUiState> = _uiState.asStateFlow()

    fun onCommunityNameChanged(value: String) {
        _uiState.update { it.copy(communityName = value, errorMessage = null) }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(communityPassword = value, errorMessage = null) }
    }

    fun onDescriptionChanged(value: String) {
        _uiState.update { it.copy(communityDescription = value, errorMessage = null) }
    }

    fun onCreateClicked() {
        val state = _uiState.value
        if (!state.isCreateEnabled) return

        viewModelScope.launch {
            _uiState.update { it.copy(isCreating = true, errorMessage = null) }

            val result = communityRepository.createCommunity(
                name = state.communityName.trim(),
                password = state.communityPassword,
                description = state.communityDescription.trim()
            )

            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isCreating = false, createSuccessful = true) }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isCreating = false,
                            errorMessage = error.localizedMessage ?: "Couldn't create that community. Try again."
                        )
                    }
                }
            )
        }
    }
}