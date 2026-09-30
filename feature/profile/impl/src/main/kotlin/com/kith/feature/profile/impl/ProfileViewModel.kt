package com.kith.feature.profile.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kith.core.data.repository.AuthRepository
import com.kith.core.data.repository.MediaRepository
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
    private val authRepository: AuthRepository,
    private val mediaRepository: MediaRepository,
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
                var finalImageUrl = newImageUrl
                val userId = authRepository.currentUserId()

                // FIX: Check if the string is a local Android file (content URI)
                if (newImageUrl != null && newImageUrl.startsWith("content://") && userId != null) {
                    val uploadResult = mediaRepository.uploadProfileImage(userId, newImageUrl)

                    if (uploadResult.isSuccess) {
                        // Replace the local URI with the Supabase public URL
                        finalImageUrl = uploadResult.getOrNull()
                    } else {
                        // Handle upload failure if necessary
                        return@launch
                    }
                }

                // Save to the database with the verified Supabase URL
                userRepository.updateProfile(
                    name = newName,
                    bio = newBio,
                    profileImageUrl = finalImageUrl
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Added Logout Functionality
    fun logout(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val result = authRepository.signOut()
            if (result.isSuccess) {
                onSuccess()
            } else {
                val exception = result.exceptionOrNull()
                exception?.printStackTrace()
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