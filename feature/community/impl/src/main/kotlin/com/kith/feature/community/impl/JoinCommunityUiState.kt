package com.kith.feature.community.impl

data class JoinCommunityUiState(
    val communityName: String = "",
    val communityPassword: String = "",
    val isJoining: Boolean = false,
    val availableCommunities: List<String> = emptyList(),
    val selectedCommunityImageUrl: String? = null,
    val errorMessage: String? = null,
    val joinSuccessful: Boolean = false
) {
    val isJoinEnabled: Boolean
        get() = communityName.isNotBlank() && communityPassword.isNotBlank() && !isJoining
}