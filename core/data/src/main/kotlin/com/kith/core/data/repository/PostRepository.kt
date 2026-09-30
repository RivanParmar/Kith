package com.kith.core.data.repository

import com.kith.core.data.Syncable
import com.kith.core.model.data.NewPostRequest
import com.kith.core.model.data.Post
import com.kith.core.model.data.PostDetail
import kotlinx.coroutines.flow.Flow

interface PostRepository : Syncable {
    fun getAllPostsStream(): Flow<List<Post>>
    fun getPostDetailStream(postId: String): Flow<PostDetail>
    suspend fun syncUserPosts(userId: String)
    suspend fun syncDataFromNetwork()
    suspend fun syncPostById(postId: String)

    suspend fun submitAnswer(postId: String, answer: String, currentUserId: String)
    suspend fun acceptSolution(postId: String)
    suspend fun rejectSolution(postId: String)
    suspend fun rateSolution(postId: String, rating: Int)
    suspend fun deletePost(postId: String)
    suspend fun createDraft(postId: String, userId: String, request: NewPostRequest)
    suspend fun publishPostToNetwork(postId: String)
    fun getPostsByUserIdStream(userId: String): Flow<List<Post>>

}