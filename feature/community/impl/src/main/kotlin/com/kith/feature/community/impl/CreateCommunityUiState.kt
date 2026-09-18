package com.kith.feature.community.impl

/**
 * Represents everything the Create Community screen needs to render itself.
 * The ViewModel is the only thing that should create/update this.
 */
data class CreateCommunityUiState(
    val communityName: String = "",
    val communityPassword: String = "",
    val communityDescription: String = "",
    val isCreating: Boolean = false,
    val errorMessage: String? = null,
    val createSuccessful: Boolean = false
) {
    /** Create button is only enabled once name and password are filled in. */
    val isCreateEnabled: Boolean
        get() = communityName.isNotBlank() && communityPassword.isNotBlank() && !isCreating
}