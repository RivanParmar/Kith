package com.kith.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.kith.core.database.model.PopulatedPostEntity
import com.kith.core.database.model.PostEntity
import com.kith.core.model.data.Post
import kotlinx.coroutines.flow.Flow

@Dao

interface PostDao {
    @Transaction
    @Query(value = "SELECT * FROM posts ORDER BY created_at DESC")
    fun getAllPostsStream(): Flow<List<PopulatedPostEntity>>
    @Transaction
    @Query(value = "SELECT * FROM posts WHERE user_id = :userId OR solver_id = :userId ORDER BY created_at DESC")
    fun getPostsByUserIdStream(userId: String): Flow<List<PopulatedPostEntity>>

    @Upsert
    suspend fun insertPost(post: PostEntity)
}
