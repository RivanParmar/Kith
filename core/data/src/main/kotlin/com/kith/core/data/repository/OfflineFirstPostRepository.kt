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
import com.kith.core.database.model.asPostDetail
import com.kith.core.database.util.PostStatus
import com.kith.core.database.util.SyncStatus
import com.kith.core.model.data.NewPostRequest
import com.kith.core.model.data.Post
import com.kith.core.model.data.PostDetail
import com.kith.core.network.KithNetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.time.Clock

class OfflineFirstPostRepository @Inject constructor(
    private val postDao: PostDao,
    private val userDao: UserDao,
    private val communityDao: CommunityDao,
    private val networkDataSource: KithNetworkDataSource
) : PostRepository {

    override fun getAllPostsStream(): Flow<List<Post>> {
        return postDao.getAllPostsStream().map { entities ->
            entities.map { it.asExternalModel() }
        }
    }

    override fun getPostDetailStream(postId: String): Flow<PostDetail> {
        return postDao.getPostDetailStream(postId).map { populatedPostEntity ->
            populatedPostEntity.asPostDetail()
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

    override suspend fun syncPostById(postId: String) {
        val networkPost = networkDataSource.getPostById(postId)
        val user = networkDataSource.getUserById(networkPost.userId)
        val community = networkDataSource.getCommunityById(networkPost.communityId)

        userDao.upsertUser(user.asUserEntity())
        communityDao.upsertCommunityPreservingStatus(community.asCommunityEntity())
        postDao.insertPost(networkPost.asEntity())
    }

    override suspend fun submitAnswer(postId: String, answer: String, currentUserId: String) {
        networkDataSource.submitAnswer(postId, answer, currentUserId)
        syncPostById(postId)
    }

    override suspend fun acceptSolution(postId: String) {
        try {
            networkDataSource.acceptAnswer(postId)
            val currentPost = postDao.getPostById(postId)
            if (currentPost != null) {
                postDao.insertPost(currentPost.copy(status = PostStatus.SOLVED))
            }
            syncPostById(postId)
        } catch (e: Exception) {
            Log.e("PostRepository", "Failed to accept answer on network: ${e.message}", e)
            throw e
        }
    }

    override suspend fun rejectSolution(postId: String) {
        try {
            networkDataSource.rejectAnswer(postId)
            val currentPost = postDao.getPostById(postId)
            if (currentPost != null) {
                postDao.insertPost(currentPost.copy(status = PostStatus.OPEN, answer = null))
            }
            syncPostById(postId)
        } catch (e: Exception) {
            Log.e("PostRepository", "Failed to reject answer on network: ${e.message}", e)
            throw e
        }
    }

    override suspend fun rateSolution(postId: String, rating: Int) {
        networkDataSource.rateSolution(postId, rating)
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

        // FIX: Safely parse the dynamic status
        val mappedStatus = try {
            PostStatus.valueOf(request.status)
        } catch (e: Exception) {
            PostStatus.OPEN
        }

        val entity = PostEntity(
            id = postId,
            userId = userId,
            communityId = request.communityId,
            title = request.title,
            content = request.content,
            status = mappedStatus, // FIX: Use dynamic status
            reward = request.reward,
            isInPerson = request.isInPerson,
            userImageCount = request.imageUris.size,
            userHasPdf = request.pdfUri != null,
            userHasAudio = request.audioUri != null,
            answer = null,
            solverId = request.solverId, // FIX: Assign to specific user
            solverImageCount = 0,
            solverHasPdf = false,
            solverHasAudio = false,
            createdAt = now,
            updatedAt = now,
            syncStatus = SyncStatus.PENDING_CREATE,
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

    override suspend fun deletePost(postId: String) {
        networkDataSource.deletePost(postId)
//        postDao.deletePostById(postId)
        // TODO
    }
}