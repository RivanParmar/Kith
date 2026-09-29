package com.kith.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.kith.core.database.model.CommunityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CommunityDao {
    @Upsert
    suspend fun upsertCommunity(community: CommunityEntity)

    @Query("UPDATE communities SET is_joined_by_me = 1 WHERE id IN (:communityIds)")
    suspend fun markAsJoined(communityIds: List<String>)

    @Query("SELECT COUNT(*) FROM communities WHERE is_joined_by_me = 1")
    fun getJoinedCommunitiesCountStream(): Flow<Int>

    @Query("SELECT COUNT(*) FROM communities WHERE is_joined_by_me = 1")
    fun getJoinedCommunitiesCount(): Int

    @Query("UPDATE communities SET is_joined_by_me = 0 WHERE id = :communityId")
    suspend fun markAsLeft(communityId: String)

    @Query("DELETE FROM communities WHERE id = :communityId")
    suspend fun deleteCommunityLocally(communityId: String)

    @Query("SELECT * FROM communities WHERE is_joined_by_me = 1 ORDER BY name ASC")
    fun getJoinedCommunitiesStream(): Flow<List<CommunityEntity>>

    @Query("SELECT * FROM communities WHERE id = :communityId")
    fun getCommunityByIdStream(communityId: String): Flow<CommunityEntity>

    @Query("SELECT is_joined_by_me FROM communities WHERE id = :id")
    suspend fun getIsJoinedByMe(id: String): Boolean?

    @Transaction
    suspend fun upsertCommunityPreservingStatus(community: CommunityEntity) {
        val existingStatus = getIsJoinedByMe(community.id) ?: false

        val safeEntity = community.copy(isJoinedByMe = existingStatus)

        upsertCommunity(safeEntity)
    }
}