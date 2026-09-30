package com.kith.core.data.repository

import android.util.Log
import com.kith.core.common.network.Dispatcher
import com.kith.core.common.network.KithDispatchers
import com.kith.core.data.Synchronizer
import com.kith.core.data.model.asExternalModel
import com.kith.core.database.dao.CommunityDao
import com.kith.core.database.model.CommunityEntity
import com.kith.core.database.model.asExternalModel
import com.kith.core.model.data.Community
import com.kith.core.model.data.User
import com.kith.core.network.KithAuthDataSource
import com.kith.core.network.KithNetworkDataSource
import com.kith.core.network.model.NetworkCommunity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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

    override suspend fun hasJoinedAnyCommunitySync(): Boolean = withContext(ioDispatcher) {
        communityDao.getJoinedCommunitiesCount() > 0
    }

    override fun getAvailableCommunities(): Flow<List<Community>> = flow {
        try {
            val remoteCommunities = networkDataSource.getCommunities().map { it.asExternalModel() }
            emit(remoteCommunities)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(emptyList())
        }
    }.flowOn(ioDispatcher)

    override fun getJoinedCommunitiesStream(): Flow<List<Community>> {
        return communityDao.getJoinedCommunitiesStream().map { entities ->
            entities.map { it.asExternalModel() }
        }
    }

    override fun getCommunityByIdStream(id: String): Flow<Community> {
        return communityDao.getCommunityByIdStream(id).map { it.asExternalModel() }
    }

    override suspend fun attemptJoinCommunity(communityId: String, password: String): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                val isSuccess = networkDataSource.joinCommunity(communityId, password)

                if (isSuccess) {
                    communityDao.markAsJoined(listOf(communityId))
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Incorrect community password"))
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
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

            val networkCommunity = NetworkCommunity(
                id = UUID.randomUUID().toString(),
                name = name,
                description = description,
                password = password,
                imageUrl = null,
                userId = currentUser,
                updatedAt = Clock.System.now(),
            )
            val createdCommunity = networkDataSource.createCommunity(networkCommunity)

            communityDao.upsertCommunity(
                CommunityEntity(
                    id = createdCommunity.id,
                    name = createdCommunity.name,
                    description = createdCommunity.description,
                    imageUrl = createdCommunity.imageUrl,
                    creatorId = currentUser,
                    updatedAt = createdCommunity.updatedAt,
                    isJoinedByMe = true,
                )
            )

            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.d("CREATE_COMMUNITY", e.toString())
            Result.failure(Exception("Network error. Must be online to create a community.", e))
        }
    }

    override suspend fun leaveCommunity(communityId: String): Result<Unit> = withContext(ioDispatcher) {
        try {
            val isSuccess = networkDataSource.leaveCommunity(communityId)
            if (isSuccess) {
                communityDao.markAsLeft(communityId)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to leave community on server"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteCommunity(communityId: String): Result<Unit> = withContext(ioDispatcher) {
        try {
            val isSuccess = networkDataSource.deleteCommunity(communityId)
            if (isSuccess) {
                communityDao.deleteCommunityLocally(communityId)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete community on server"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateCommunityDescription(communityId: String, description: String): Result<Unit> = withContext(ioDispatcher) {
        try {
            networkDataSource.updateCommunityDescription(communityId, description)
            val localEntity = communityDao.getCommunityByIdStream(communityId).first()
            communityDao.upsertCommunity(localEntity.copy(description = description))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // NEW: Execute search and map network model to domain model
    // NEW: Execute search and map network model to domain model
    override suspend fun searchCommunityMembers(communityId: String, query: String): Result<List<User>> =
        withContext(ioDispatcher) {
            try {
                val networkUsers = networkDataSource.searchUsers(communityId, query)
                // Assuming networkUser.asExternalModel() extension exists based on your architecture pattern
                val profiles = networkUsers.map { it.asExternalModel() }

                // Explicitly define the generic type to fix the compiler inference error
                Result.success(profiles)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // Explicitly define the generic type here as well
                Result.failure(e)
            }
        }

    override suspend fun syncJoinedCommunities(): Result<Unit> = withContext(ioDispatcher) {
        try {
            val currentUserId = authDataSource.currentUserId()
                ?: return@withContext Result.failure(Exception("User not authenticated"))

            val remoteCommunities = networkDataSource.getJoinedCommunities(currentUserId)

            if (remoteCommunities.isNotEmpty()) {
                // Upsert all joined communities into Room marked as joined
                remoteCommunities.forEach { networkCommunity ->
                    communityDao.upsertCommunity(
                        CommunityEntity(
                            id = networkCommunity.id,
                            name = networkCommunity.name,
                            description = networkCommunity.description,
                            imageUrl = networkCommunity.imageUrl,
                            updatedAt = networkCommunity.updatedAt,
                            isJoinedByMe = true,
                            creatorId = networkCommunity.userId,
                        )
                    )
                }
            }

            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean {
        return syncJoinedCommunities().isSuccess
    }
}