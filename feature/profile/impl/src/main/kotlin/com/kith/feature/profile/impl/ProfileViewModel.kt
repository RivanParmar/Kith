package com.kith.feature.profile.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kith.core.data.repository.UserRepository
import com.kith.core.model.data.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = userRepository.getUserProfileStream()
        .map {
        if (it != null) {
            ProfileUiState.Success(userProfile = it)
        } else {
            ProfileUiState.Error("User profile not found")
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProfileUiState.Loading,
    )

    fun saveProfile(newName: String, newBio: String, newImageUrl: String?) {
        viewModelScope.launch {
            try {
                userRepository.updateProfile(
                    name = newName,
                    bio = newBio,
                    profileImageUrl = newImageUrl
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

sealed interface ProfileUiState {
    data object Loading : ProfileUiState

    data class Success(
        val userProfile: UserProfile,
    ) : ProfileUiState

    data class Error(val message: String) : ProfileUiState
}