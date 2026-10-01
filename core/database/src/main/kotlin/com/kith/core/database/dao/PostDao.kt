package com.kith.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.kith.core.database.model.PopulatedPostEntity
import com.kith.core.database.model.PostEntity
import com.kith.core.database.util.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao

interface PostDao {
    @Transaction
    @Query(value = "SELECT * FROM posts ORDER BY created_at DESC")
    fun getAllPostsStream(): Flow<List<PopulatedPostEntity>>
    @Transaction
    @Query(value = "SELECT * FROM posts WHERE user_id = :userId OR solver_id = :userId ORDER BY created_at DESC")
    fun getPostsByUserIdStream(userId: String): Flow<List<PopulatedPostEntity>>

    @Transaction
    @Query(value = "SELECT * FROM posts WHERE id = :postId")
    fun getPostDetailStream(postId: String): Flow<PopulatedPostEntity>

    @Upsert
    suspend fun insertPost(post: PostEntity)

    @Query(value = "SELECT * FROM posts WHERE id = :postId")
    suspend fun getPostById(postId: String): PostEntity?

    @Query(value = "UPDATE posts SET sync_status = :syncStatus WHERE id = :postId")
    suspend fun updateSyncStatus(postId: String, syncStatus: SyncStatus)

    @Query(value = "SELECT * FROM posts WHERE sync_status = :syncStatus")
    suspend fun getPostsBySyncStatus(syncStatus: SyncStatus): List<PostEntity>

    @Query(value = "DELETE FROM posts WHERE id = :postId")
    suspend fun deletePostById(postId: String)
}
