package com.kith.core.data.repository

import android.util.Log
import com.kith.core.common.network.Dispatcher
import com.kith.core.common.network.KithDispatchers
import com.kith.core.data.model.asExternalModel
import com.kith.core.database.dao.CommunityDao
import com.kith.core.database.model.CommunityEntity
import com.kith.core.model.data.Community
import com.kith.core.network.KithAuthDataSource
import com.kith.core.network.KithNetworkDataSource
import com.kith.core.network.model.NetworkCommunity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Clock

class OfflineFirstCommunityRepository @Inject constructor(
    private val networkDataSource: KithNetworkDataSource,
    private val authDataSource: KithAuthDataSource,
    private val communityDao: CommunityDao,
    @Dispatcher(KithDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : CommunityRepository {

    override val hasJoinedAnyCommunity: Flow<Boolean> =
        communityDao.getJoinedCommunitiesCountStream().map { count -> count > 0 }

    override fun getAvailableCommunities(): Flow<List<Community>> = flow {
        try {
            val remoteCommunities = networkDataSource.getCommunities().map { it.asExternalModel() }
            emit(remoteCommunities)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // Emits empty list if the server cannot be reached
            emit(emptyList())
        }
    }.flowOn(ioDispatcher)

    override suspend fun attemptJoinCommunity(communityId: String, password: String): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                // 1. The Strict Online Call (No WorkManager here)
                val isSuccess = networkDataSource.joinCommunity(communityId, password)

                if (isSuccess) {
                    // 2. Update the local Room cache instantly so the UI reacts
                    communityDao.markAsJoined(listOf(communityId))
                    Result.success(Unit)
                } else {
                    // 3. Wrong password or rejected
                    Result.failure(Exception("Incorrect community password"))
                }
            } catch (e: CancellationException) {
                // IMPORTANT: Never swallow CancellationException in Coroutines
                throw e
            } catch (e: Exception) {
                // Catches "No Internet" / socket / HTTP exceptions from Ktor/Supabase
                Result.failure(Exception("Network error. Must be online to join a community.", e))
            }
        }

    override suspend fun createCommunity(
        name: String,
        password: String,
        description: String,
    ): Result<Unit> = withContext(ioDispatcher) {
        try {
            val currentUser = authDataSource.currentUserId() ?: return@withContext Result.failure(
                Exception("Not logged in!")
            )

            // 1. The Strict Online Call to create the community on the server
            val networkCommunity = NetworkCommunity(
                id = UUID.randomUUID().toString(),
                name = name,
                description = description,
                password = password,
                imageUrl = null,
                userId = currentUser,
                updatedAt = Clock.System.now(),
            )
            val createdCommunity = networkDataSource.createCommunity(
                networkCommunity
            )

            // 2. Update the local Room cache instantly and mark as joined by creator
            communityDao.upsertCommunity(
                CommunityEntity(
                    id = createdCommunity.id,
                    name = createdCommunity.name,
                    description = createdCommunity.description,
                    imageUrl = createdCommunity.imageUrl,
                    updatedAt = createdCommunity.updatedAt,
                    isJoinedByMe = true,
                )
            )

            Result.success(Unit)
        } catch (e: CancellationException) {
            // IMPORTANT: Never swallow CancellationException in Coroutines
            throw e
        } catch (e: Exception) {
            // Catches "No Internet" / socket / HTTP exceptions from Ktor/Supabase
            Log.d("CREATE_COMMUNITY", e.toString())
            Result.failure(Exception("Network error. Must be online to create a community.", e))
        }
    }
}