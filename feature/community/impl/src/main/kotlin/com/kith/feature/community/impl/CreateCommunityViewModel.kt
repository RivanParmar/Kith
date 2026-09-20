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
class CreateCommunityViewModel @Inject constructor(
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateCommunityUiState())
    val uiState: StateFlow<CreateCommunityUiState> = _uiState.asStateFlow()

    fun onCommunityNameChanged(value: String) {
        _uiState.value = _uiState.value.copy(communityName = value, errorMessage = null)
    }

    fun onPasswordChanged(value: String) {
        _uiState.value = _uiState.value.copy(communityPassword = value, errorMessage = null)
    }

    fun onDescriptionChanged(value: String) {
        _uiState.value = _uiState.value.copy(communityDescription = value)
    }

    fun onCreateClicked() {
        val state = _uiState.value
        if (!state.isCreateEnabled) return

        viewModelScope.launch {
            _uiState.value = state.copy(isCreating = true, errorMessage = null)
//            val result = communityRepository.createCommunity(
//                name = state.communityName.trim(),
//                password = state.communityPassword,
//                description = state.communityDescription.trim()
//            )
//            result.onSuccess {
//                _uiState.value = _uiState.value.copy(isCreating = false, createSuccessful = true)
//            }.onFailure { error ->
//                _uiState.value = _uiState.value.copy(
//                    isCreating = false,
//                    errorMessage = error.message ?: "Couldn't create that community. Try again."
//                )
//            }
        }
    }
}