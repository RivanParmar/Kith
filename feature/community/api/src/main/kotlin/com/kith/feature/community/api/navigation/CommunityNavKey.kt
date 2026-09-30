package com.kith.feature.community.api.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object JoinCommunityNavKey : NavKey

@Serializable
data object CreateCommunityNavKey : NavKey

@Serializable
data object CommunityListNavKey : NavKey

// UPDATED: Now accepts the communityId argument
@Serializable
data class CommunityDetailNavKey(val communityId: String) : NavKey