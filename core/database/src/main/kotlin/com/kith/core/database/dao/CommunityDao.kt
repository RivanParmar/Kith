package com.kith.core.database.dao

import androidx.room.Dao
import androidx.room.Query
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
}