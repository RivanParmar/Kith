package com.kith.feature.profile.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kith.core.data.repository.UserRepository
import com.kith.core.model.data.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository // 1. Inject the repository
) : ViewModel() {

    // 2. Separate local UI state from Database state
    private val _isEditDialogVisible = MutableStateFlow(false)

    // 3. Combine the Database stream with the local UI state stream
    val uiState: StateFlow<ProfileUiState> = combine(
        userRepository.getUserProfileStream(),
        _isEditDialogVisible
    ) { profile, isEditVisible ->
        if (profile != null) {
            ProfileUiState.Success(
                userProfile = profile,
                isEditDialogVisible = isEditVisible
            ) as ProfileUiState
        } else {
            ProfileUiState.Error("User profile not found") as ProfileUiState
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProfileUiState.Loading,
    )

//    val uiState: StateFlow<ProfileUiState> = flow {
//        val profileFlow = userRepository.getUserProfileStream()
//
//        val combinedFlow = combine(profileFlow, _isEditDialogVisible) { profile, isVisible ->
//            ProfileUiState.Success(profile!!, isVisible)
//        }
//
//        emit(combinedFlow as ProfileUiState)
//    }.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5_000),
//        initialValue = ProfileUiState.Loading,
//    )

    init {
        // 4. Trigger network sync on initialization
        viewModelScope.launch {
            try {
                userRepository.syncCurrentUser()
            } catch (e: Exception) {
                e.printStackTrace() // Handled gracefully as local DB serves cached data
            }
        }
    }

    fun showEditDialog() {
        _isEditDialogVisible.value = true
    }

    fun hideEditDialog() {
        _isEditDialogVisible.value = false
    }

    fun saveProfile(newName: String, newBio: String, newImageUrl: String?) {
        viewModelScope.launch {
            try {
                // Call the repository to sync the changes
                userRepository.updateProfile(
                    name = newName,
                    bio = newBio,
                    profileImageUrl = newImageUrl
                )

                // Hide the dialog only if the network and database updates succeed
                _isEditDialogVisible.value = false
            } catch (e: Exception) {
                // If offline or Supabase fails, the exception is caught here.
                // The dialog stays open so the user can try again later.
                e.printStackTrace()
            }
        }
    }
}

