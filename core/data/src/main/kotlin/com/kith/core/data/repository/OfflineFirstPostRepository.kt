package com.kith.core.data.repository

import android.util.Log
import com.kith.core.data.model.asCommunityEntity
import com.kith.core.data.model.asEntity
import com.kith.core.data.model.asNetworkModel
import com.kith.core.data.model.asUserEntity
import com.kith.core.database.dao.CommunityDao
import com.kith.core.database.dao.PostDao
import com.kith.core.database.dao.UserDao
import com.kith.core.database.model.PostEntity
import com.kith.core.database.model.asExternalModel
import com.kith.core.database.util.PostStatus
import com.kith.core.database.util.SyncStatus
import com.kith.core.model.data.NewPostRequest
import com.kith.core.model.data.Post
import com.kith.core.network.supabase.SupabaseNetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.time.Clock

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
            communityDao.upsertCommunity(community.asCommunityEntity())
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
                communityDao.upsertCommunity(community.asCommunityEntity())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        userPosts.forEach { networkPost ->
            postDao.insertPost(networkPost.asEntity())
        }
    }

    override suspend fun createDraft(
        postId: String,
        userId: String,
        request: NewPostRequest,
    ) {
        val now = Clock.System.now()

        val syncStatus = if (request.isDraft) SyncStatus.DRAFT else SyncStatus.PENDING_CREATE

        val entity = PostEntity(
            id = postId,
            userId = userId,
            communityId = request.communityId,
            title = request.title,
            content = request.content,
            status = PostStatus.OPEN,
            reward = request.reward,
            isInPerson = request.isInPerson,
            userImageCount = request.imageUris.size,
            userHasPdf = request.pdfUri != null,
            userHasAudio = request.audioUri != null,
            answer = null,
            solverId = null,
            solverImageCount = 0,
            solverHasPdf = false,
            solverHasAudio = false,
            createdAt = now,
            updatedAt = now,
            syncStatus = syncStatus,
        )
        Log.d("CREATE", "Created entity!")

        syncDataFromNetwork()
        postDao.insertPost(entity)
        Log.d("CREATE", "Inserted into database!")
    }

    override suspend fun publishPostToNetwork(postId: String) {
        val entity = postDao.getPostById(postId) ?: return

        try {
            networkDataSource.createPost(entity.asNetworkModel())

            postDao.updateSyncStatus(postId, SyncStatus.SYNCED)
            Log.d("CREATE", "Success!")
        } catch (e: Exception) {
            // Leave as PENDING_CREATE for background SyncManager to retry
            Log.d("CREATE", "Failed!")
            Log.d("CREATE", e.toString())
        }
    }
}
