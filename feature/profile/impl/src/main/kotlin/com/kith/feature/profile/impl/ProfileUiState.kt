package com.kith.feature.profile.impl

import com.kith.core.model.data.UserProfile

sealed interface ProfileUiState {
    data object Loading : ProfileUiState

    data class Success(
        val userProfile: UserProfile,
        val isEditDialogVisible: Boolean = false
    ) : ProfileUiState

    data class Error(val message: String) : ProfileUiState
}