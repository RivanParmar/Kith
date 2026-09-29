package com.kith.core.data.repository

import com.kith.core.data.model.asUserEntity
import com.kith.core.database.dao.UserDao
import com.kith.core.database.model.asUserProfile
import com.kith.core.model.data.UserProfile
import com.kith.core.network.KithAuthDataSource
import com.kith.core.network.KithNetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OfflineFirstUserRepository @Inject constructor(
    private val userDao: UserDao,
    private val networkDataSource: KithNetworkDataSource,
    private val authDataSource: KithAuthDataSource,
) : UserRepository {

    override fun getUserProfileStream(): Flow<UserProfile?> {
        val userId = authDataSource.currentUserId() ?: return flowOf(null)

        return userDao.getUserStream(userId).map { entity ->
            entity?.asUserProfile()
        }
    }

    override suspend fun syncCurrentUser() {
        val userId = authDataSource.currentUserId() ?: return

        val networkUser = networkDataSource.getUserById(userId)
        userDao.upsertUser(networkUser.asUserEntity())
    }


    override suspend fun updateProfile(name: String, bio: String, profileImageUrl: String?) {
        val userId = authDataSource.currentUserId() ?: return

        // 1. Network First: Update Supabase
        networkDataSource.updateUserProfile(userId, name, bio, profileImageUrl)

        // 2. Local Second: Update Room Cache
        // (Because Room returns a Flow, this instantly triggers your ViewModel to update the UI)
        userDao.updateProfile(userId, name, bio, profileImageUrl)
    }

    override suspend fun syncFcmToken(token: String) {
        syncFcmToken(token)
    }
}