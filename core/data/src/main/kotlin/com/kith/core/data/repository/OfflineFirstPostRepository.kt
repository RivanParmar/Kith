package com.kith.core.data.repository

import android.util.Log
import com.kith.core.data.Synchronizer
import com.kith.core.data.model.asCommunityEntity
import com.kith.core.data.model.asEntity
import com.kith.core.data.model.asNetworkModel
import com.kith.core.data.model.asUserEntity
import com.kith.core.data.suspendRunCatching
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
import java.util.Locale.filter
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

        val uniqueUserIds = (networkPosts.map { it.userId } + networkPosts.mapNotNull { it.solverId })
            .filter { it.isNotBlank() }
            .toSet()
        val uniqueCommunityIds = networkPosts.map { it.communityId }.toSet()

        uniqueUserIds.forEach { id ->
            try {
                val user = networkDataSource.getUserById(id)
                userDao.upsertUser(user.asUserEntity())
            } catch (e: Exception) {
                Log.e("PostRepository", "Failed to fetch user $id", e)
            }
        }

        uniqueCommunityIds.forEach { id ->
            try {
                val community = networkDataSource.getCommunityById(id)
                communityDao.upsertCommunityPreservingStatus(community.asCommunityEntity())
            } catch (e: Exception) {
                Log.e("PostRepository", "Failed to fetch community $id", e)
            }
        }

        networkPosts.forEach { networkPost ->
            postDao.insertPost(networkPost.asEntity())
        }
    }

    override suspend fun syncPostById(postId: String) {
        val networkPost = networkDataSource.getPostById(postId)
        try {
            val user = networkDataSource.getUserById(networkPost.userId)
            userDao.upsertUser(user.asUserEntity())
        } catch (e: Exception) {
            Log.e("PostRepository", "Failed to fetch post author ${networkPost.userId}", e)
        }

        try {
            val community = networkDataSource.getCommunityById(networkPost.communityId)
            communityDao.upsertCommunityPreservingStatus(community.asCommunityEntity())
        } catch (e: Exception) {
            Log.e("PostRepository", "Failed to fetch community ${networkPost.communityId}", e)
        }

        networkPost.solverId?.let { safeSolverId ->
            if (safeSolverId.isNotBlank()) {
                try {
                    val solver = networkDataSource.getUserById(safeSolverId)
                    userDao.upsertUser(solver.asUserEntity())
                } catch (e: Exception) {
                    Log.e("PostRepository", "Failed to fetch solver user $safeSolverId", e)
                }
            }
        }

        postDao.insertPost(networkPost.asEntity())
    }

    override suspend fun submitAnswer(postId: String, answer: String, solverId: String) {
        try {
            val currentPost = postDao.getPostById(postId)
            if (currentPost != null) {
                postDao.insertPost(currentPost.copy(answer = answer, solverId = solverId))
            }
        } catch (_: Exception) {}

        networkDataSource.submitAnswer(postId, answer, solverId)
        syncPostById(postId)
    }

    override suspend fun acceptSolution(postId: String) {
        try {
            val currentPost = postDao.getPostById(postId)
            if (currentPost != null) {
                postDao.insertPost(currentPost.copy(status = PostStatus.SOLVED))
            }
            networkDataSource.acceptAnswer(postId)
            syncPostById(postId)
        } catch (e: Exception) {
            Log.e("PostRepository", "Failed to accept answer on network: ${e.message}", e)
            throw e
        }
    }

    override suspend fun rejectSolution(postId: String) {
        try {
            val currentPost = postDao.getPostById(postId)
            if (currentPost != null) {
                postDao.insertPost(currentPost.copy(status = PostStatus.OPEN, answer = null))
            }
            networkDataSource.rejectAnswer(postId)
            syncPostById(postId)
        } catch (e: Exception) {
            Log.e("PostRepository", "Failed to reject answer on network: ${e.message}", e)
            throw e
        }
    }

    override suspend fun rateSolution(postId: String, rating: Float) {
        networkDataSource.rateSolution(postId, rating)
        syncPostById(postId)
    }

    override suspend fun syncUserPosts(userId: String) {
        val userPosts = networkDataSource.getPostsForUser(userId)
        if (userPosts.isEmpty()) return

        val uniqueUserIds = (userPosts.map { it.userId } + userPosts.mapNotNull { it.solverId })
            .filter { it.isNotBlank() }
            .toSet()
        uniqueUserIds.forEach { id ->
            try {
                val user = networkDataSource.getUserById(id)
                userDao.upsertUser(user.asUserEntity())
            } catch (e: Exception) {
                Log.e("PostRepository", "Failed to fetch user $id", e)
            }
        }

        val uniqueCommunityIds = userPosts.map { it.communityId }.toSet()
        uniqueCommunityIds.forEach { id ->
            try {
                val community = networkDataSource.getCommunityById(id)
                communityDao.upsertCommunity(community.asCommunityEntity())
            } catch (e: Exception) {
                Log.e("PostRepository", "Failed to fetch community $id", e)
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

        val mappedStatus = try {
            PostStatus.valueOf(request.status)
        } catch (_: Exception) {
            PostStatus.OPEN
        }

        val entity = PostEntity(
            id = postId,
            userId = userId,
            communityId = request.communityId,
            title = request.title,
            content = request.content,
            status = mappedStatus,
            reward = request.reward,
            isInPerson = request.isInPerson,
            userImageCount = request.imageUris.size,
            userHasPdf = request.pdfUri != null,
            userHasAudio = request.audioUri != null,
            answer = null,
            solverId = request.solverId,
            solverImageCount = 0,
            solverHasPdf = false,
            solverHasAudio = false,
            createdAt = now,
            updatedAt = now,
            syncStatus = SyncStatus.PENDING_CREATE,
            rating = null,
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
            // FIX 5: Actually using the 'e' parameter to log the exact error
            Log.e("CREATE", "Failed to publish post to network", e)
        }
    }

    override suspend fun deletePost(postId: String) {
        try {
            networkDataSource.deletePost(postId)
            postDao.deletePostById(postId)
        } catch (e: Exception) {
            Log.e("PostRepository", "Failed to delete post: ${e.message}", e)
            throw e
        }
    }

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean {
        return suspendRunCatching {
            syncDataFromNetwork()
        }.isSuccess
    }
}