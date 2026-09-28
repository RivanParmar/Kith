package com.kith.core.data.repository

import com.kith.core.model.data.Community
import kotlinx.coroutines.flow.Flow

interface CommunityRepository {
    val hasJoinedAnyCommunity: Flow<Boolean>
    fun getAvailableCommunities(): Flow<List<Community>>
    suspend fun attemptJoinCommunity(communityId: String, password: String): Result<Unit>
    suspend fun createCommunity(
        name: String,
        password: String,
        description: String,
    ): Result<Unit>
}