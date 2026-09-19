package com.kith.core.database.dao

import androidx.room.Dao
import androidx.room.Upsert
import com.kith.core.database.model.CommunityEntity

@Dao
interface CommunityDao {
    @Upsert
    suspend fun upsertCommunity(community: CommunityEntity)
}