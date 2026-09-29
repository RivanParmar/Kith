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

        try {
            val networkUser = networkDataSource.getUserById(userId)
            userDao.upsertUser(networkUser.asUserEntity())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun updateProfile(name: String, bio: String, profileImageUrl: String?) {
        val userId = authDataSource.currentUserId() ?: return

        // 1. Local First: Instantly update Room.
        // This triggers your UI StateFlow to update immediately without waiting for the network.
        userDao.updateProfile(userId, name, bio, profileImageUrl)

        // 2. Network Second: Push the changes to Supabase in the background.
        try {
            networkDataSource.updateUserProfile(userId, name, bio, profileImageUrl)
        } catch (e: Exception) {
            // If the network fails, it won't crash the app. The local DB will retain the changes.
            e.printStackTrace()
        }
    }

    override suspend fun syncFcmToken(token: String) {
        syncFcmToken(token)
    }
}