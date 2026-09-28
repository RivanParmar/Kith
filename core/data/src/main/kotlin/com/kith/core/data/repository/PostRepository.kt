package com.kith.core.data.repository

import com.kith.core.model.data.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    fun getAllPostsStream(): Flow<List<Post>>
    suspend fun syncUserPosts(userId: String)
    suspend fun syncDataFromNetwork()
    fun getPostsByUserIdStream(userId: String): Flow<List<Post>>

}