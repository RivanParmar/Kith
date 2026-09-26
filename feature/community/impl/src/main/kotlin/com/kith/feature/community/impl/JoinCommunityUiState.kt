package com.kith.feature.community.impl

/**
 * Represents everything the Join Community screen needs to render itself.
 * The ViewModel is the only thing that should create/update this.
 */
data class JoinCommunityUiState(
    val communityName: String = "",
    val communityPassword: String = "",
    val isJoining: Boolean = false,
    val availableCommunities: List<String> = emptyList(),
    val errorMessage: String? = null,
    val joinSuccessful: Boolean = false
) {
    /** Join button is only enabled once both fields have something in them. */
    val isJoinEnabled: Boolean
        get() = communityName.isNotBlank() && communityPassword.isNotBlank() && !isJoining
}


sealed interface JoinCommunityEvent {
    data object NavigateToHome : JoinCommunityEvent
    data class ShowToast(val message: String) : JoinCommunityEvent
}