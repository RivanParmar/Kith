package com.kith.feature.profile.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kith.core.model.data.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {

            _uiState.value = ProfileUiState.Success(
                userProfile = UserProfile(
                    id = "u_1",
                    name = "Mr X",
                    profileImageUrl = null,
                    bio = "B.Tech CSE Student at Darshan University",
                    xp = 2400,
                    rating = 4.9f,
                    problemsAsked = 12,
                    problemsSolved = 42,
                    isPremium = true
                ),
                email = "akshil@example.com",
                isEditDialogVisible = false
            )
        }
    }

    fun showEditDialog() {
        _uiState.update { currentState ->
            if (currentState is ProfileUiState.Success) {
                currentState.copy(isEditDialogVisible = true)
            } else currentState
        }
    }

    fun hideEditDialog() {
        _uiState.update { currentState ->
            if (currentState is ProfileUiState.Success) {
                currentState.copy(isEditDialogVisible = false)
            } else currentState
        }
    }

    fun saveProfile(newName: String, newBio: String, newImageUrl: String?) {
        _uiState.update { currentState ->
            if (currentState is ProfileUiState.Success) {
                // Update local state instantly.
                // In production, call repository.updateProfile() here.
                val updatedProfile = currentState.userProfile.copy(
                    name = newName,
                    bio = newBio,
                    profileImageUrl = newImageUrl
                )
                currentState.copy(
                    userProfile = updatedProfile,
                    isEditDialogVisible = false
                )
            } else currentState
        }
    }
}

