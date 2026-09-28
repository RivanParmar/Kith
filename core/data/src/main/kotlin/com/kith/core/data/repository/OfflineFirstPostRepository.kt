package com.kith.core.data.repository

import com.kith.core.data.model.asCommunityEntity
import com.kith.core.data.model.asEntity
import com.kith.core.data.model.asUserEntity
import com.kith.core.database.dao.CommunityDao
import com.kith.core.database.dao.PostDao
import com.kith.core.database.dao.UserDao
import com.kith.core.database.model.asExternalModel
import com.kith.core.model.data.Post
import com.kith.core.network.supabase.SupabaseNetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OfflineFirstPostRepository @Inject constructor(
    private val postDao: PostDao,
    private val userDao: UserDao,
    private val communityDao: CommunityDao,
    private val networkDataSource: SupabaseNetworkDataSource
) : PostRepository {

    override fun getAllPostsStream(): Flow<List<Post>> {
        return postDao.getAllPostsStream().map { entities ->
            entities.map { it.asExternalModel() }
        }
    }

    override fun getPostsByUserIdStream(userId: String): Flow<List<Post>> {
        return postDao.getPostsByUserIdStream(userId).map { entities ->
            entities.map { it.asExternalModel() }
        }
    }

    override suspend fun syncDataFromNetwork() {
        val networkPosts = networkDataSource.getPosts(limit = 20)

        val uniqueUserIds = networkPosts.map { it.userId }.toSet()
        val uniqueCommunityIds = networkPosts.map { it.communityId }.toSet()

        uniqueUserIds.forEach { id ->
            val user = networkDataSource.getUserById(id)
            userDao.upsertUser(user.asUserEntity())
        }

        uniqueCommunityIds.forEach { id ->
            val community = networkDataSource.getCommunityById(id)
            val associatedUserId = networkPosts.first { it.communityId == id }.userId
            communityDao.upsertCommunity(community.asCommunityEntity(associatedUserId))
        }

        networkPosts.forEach { networkPost ->
            postDao.insertPost(networkPost.asEntity())
        }
    }

    override suspend fun syncUserPosts(userId: String) {
        val userPosts = networkDataSource.getPostsForUser(userId)
        if (userPosts.isEmpty()) return

        val uniqueCommunityIds = userPosts.map { it.communityId }.toSet()
        uniqueCommunityIds.forEach { id ->
            try {
                val community = networkDataSource.getCommunityById(id)
                communityDao.upsertCommunity(community.asCommunityEntity(userId))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        userPosts.forEach { networkPost ->
            postDao.insertPost(networkPost.asEntity())
        }
    }
}