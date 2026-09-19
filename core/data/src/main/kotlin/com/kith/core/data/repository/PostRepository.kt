package com.kith.core.data.repository

import com.kith.core.model.data.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    fun getAllPostsStream(): Flow<List<Post>>
    suspend fun syncDataFromNetwork()
}