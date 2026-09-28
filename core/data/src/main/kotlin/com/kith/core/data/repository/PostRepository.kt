package com.kith.core.data.repository

import com.kith.core.model.data.NewPostRequest
import com.kith.core.model.data.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    fun getAllPostsStream(): Flow<List<Post>>
    suspend fun syncUserPosts(userId: String)
    suspend fun syncDataFromNetwork()
    suspend fun createDraft(postId: String, userId: String, request: NewPostRequest)
    suspend fun publishPostToNetwork(postId: String)
    fun getPostsByUserIdStream(userId: String): Flow<List<Post>>

}