package com.kith.core.data.repository

import com.kith.core.data.Syncable
import com.kith.core.model.data.Community
import kotlinx.coroutines.flow.Flow

interface CommunityRepository : Syncable {
    val hasJoinedAnyCommunity: Flow<Boolean>
    suspend fun hasJoinedAnyCommunitySync(): Boolean
    fun getAvailableCommunities(): Flow<List<Community>>
    fun getJoinedCommunitiesStream(): Flow<List<Community>>
    fun getCommunityByIdStream(id: String): Flow<Community>

    suspend fun attemptJoinCommunity(communityId: String, password: String): Result<Unit>
    suspend fun leaveCommunity(communityId: String): Result<Unit>
    suspend fun deleteCommunity(communityId: String): Result<Unit>
    suspend fun updateCommunityDescription(communityId: String, description: String): Result<Unit>
    // Add this inside the interface:
    suspend fun searchCommunityMembers(communityId: String, query: String): Result<List<com.kith.core.model.data.User>>

    suspend fun createCommunity(
        name: String,
        password: String,
        description: String,
    ): Result<Unit>

    suspend fun syncJoinedCommunities(): Result<Unit>
}