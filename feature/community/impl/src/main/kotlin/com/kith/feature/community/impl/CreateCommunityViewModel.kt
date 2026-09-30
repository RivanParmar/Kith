package com.kith.feature.community.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kith.core.data.repository.CommunityRepository
import com.kith.core.data.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateCommunityViewModel @Inject constructor(
    private val communityRepository: CommunityRepository,
    private val mediaRepository: MediaRepository // ADDED
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

    fun onImagePicked(uri: String) {
        _uiState.update { it.copy(communityImageUri = uri) }
    }

    fun onCreateClicked() {
        val state = _uiState.value
        if (!state.isCreateEnabled) return

        viewModelScope.launch {
            _uiState.update { it.copy(isCreating = true, errorMessage = null) }

            // Upload the image first if selected
            var finalImageUrl: String? = null
            if (state.communityImageUri != null) {
                // Generate a temporary UUID. When the community is updated later,
                // it will overwrite securely using the actual Community ID.
                val tempId = UUID.randomUUID().toString()
                val uploadResult = mediaRepository.uploadCommunityImage(tempId, state.communityImageUri)
                finalImageUrl = uploadResult.getOrNull()
            }

            val result = communityRepository.createCommunity(
                name = state.communityName.trim(),
                password = state.communityPassword,
                description = state.communityDescription.trim(),
                imageUrl = finalImageUrl // Ensure your CommunityRepository accepts this
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