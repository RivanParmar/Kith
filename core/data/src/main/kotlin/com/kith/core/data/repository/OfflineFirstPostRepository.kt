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

    // Added 'override' keyword
    override fun getAllPostsStream(): Flow<List<Post>> {
        return postDao.getAllPostsStream().map { entities ->
            entities.map { it.asExternalModel() }
        }
    }

    // Added 'override' keyword
    override suspend fun syncDataFromNetwork() {
        // 1. Fetch posts (pass whatever limit makes sense for your UI)
        val networkPosts = networkDataSource.getPosts(limit = 20)

        // 2. Extract unique IDs so we don't spam the network with duplicate requests
        val uniqueUserIds = networkPosts.map { it.userId }.toSet()
        val uniqueCommunityIds = networkPosts.map { it.communityId }.toSet()

        // 3. Dynamically fetch and insert missing Users
        uniqueUserIds.forEach { id ->
            val user = networkDataSource.getUserById(id)
            userDao.upsertUser(user.asUserEntity())
        }

        // 4. Dynamically fetch and insert missing Communities
        uniqueCommunityIds.forEach { id ->
            val community = networkDataSource.getCommunityById(id)
            // Finds the userId of the first post that requested this community to satisfy your mapper function
            val associatedUserId = networkPosts.first { it.communityId == id }.userId
            communityDao.upsertCommunity(community.asCommunityEntity(associatedUserId))
        }

        // 5. Insert Posts LAST to satisfy Room's Foreign Key constraints
        networkPosts.forEach { networkPost ->
            postDao.insertPost(networkPost.asEntity())
        }
    }
}
