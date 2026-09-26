package com.kith.feature.community.impl

data class CreateCommunityUiState(
    val communityName: String = "",
    val communityPassword: String = "",
    val communityDescription: String = "",
    val isCreating: Boolean = false,
    val errorMessage: String? = null,
    val createSuccessful: Boolean = false
) {
    val isCreateEnabled: Boolean
        get() = communityName.isNotBlank() &&
                communityPassword.isNotBlank() &&
                communityDescription.isNotBlank() &&
                !isCreating
}