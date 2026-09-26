package com.kith.core.data.repository

import com.kith.core.network.model.NetworkCommunity
import kotlinx.coroutines.flow.Flow

interface CommunityRepository {
    fun getAvailableCommunities(): Flow<List<NetworkCommunity>>
    suspend fun attemptJoinCommunity(communityId: String, password: String): Result<Unit>
    suspend fun createCommunity(
        name: String,
        password: String,
        description: String,
    ): Result<Unit>
}