package com.kith.feature.profile.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kith.core.model.data.UserProfile
import com.kith.core.network.KithNetworkDataSource
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = OtherUserProfileViewModel.Factory::class)
class OtherUserProfileViewModel @AssistedInject constructor(
    @Assisted val userId: String,
    private val networkDataSource: KithNetworkDataSource
) : ViewModel() {

    private val _uiState = MutableStateFlow<OtherUserProfileUiState>(OtherUserProfileUiState.Loading)
    val uiState: StateFlow<OtherUserProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                // Fetch the user from your backend
                val networkUser = networkDataSource.getUserById(userId)

                // Map the backend model to your UI model
                val profile = UserProfile(
                    id = networkUser.id,
                    name = networkUser.name,
                    profileImageUrl = networkUser.profileImageUrl,
                    bio = networkUser.bio,
                    // If your NetworkUser has different property names for these, update them here:
                    xp = 0, // Replace with networkUser.xp if available
                    rating = 5.0f, // Replace with networkUser.rating if available
                    problemsAsked = 0,
                    problemsSolved = 0,
                    isPremium = false
                )
                _uiState.value = OtherUserProfileUiState.Success(profile)
            } catch (e: Exception) {
                _uiState.value = OtherUserProfileUiState.Error("Failed to load profile")
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(userId: String): OtherUserProfileViewModel
    }
}

sealed interface OtherUserProfileUiState {
    data object Loading : OtherUserProfileUiState
    data class Error(val message: String?) : OtherUserProfileUiState
    data class Success(val profile: UserProfile) : OtherUserProfileUiState
}
