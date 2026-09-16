package com.kith.core.data.repository

import com.kith.core.database.dao.UserDao
import com.kith.core.network.KithAuthDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OfflineFirstAuthRepository @Inject constructor(
    private val authDataSource: KithAuthDataSource,
    private val userDao: UserDao // Your Room database DAO
) : AuthRepository {

    // Pass-through the session flow directly from the remote source
    override val sessionStatus: Flow<Boolean> = authDataSource.sessionStatus

    override suspend fun currentUserId(): String? {
        return authDataSource.currentUserId()
    }

    override suspend fun signUp(email: String, password: String, displayName: String?): Result<Unit> {
        return authDataSource.signUp(email, password, displayName).onSuccess {
            // Once signed up, fetch the newly created user ID
            val userId = authDataSource.currentUserId() ?: return@onSuccess

            // Cache the basic user profile into the local Room database immediately

        }
    }

    override suspend fun signIn(email: String, password: String): Result<Unit> {
        return authDataSource.signIn(email, password).onSuccess {
            // NOTE: In a complete offline-first app, you would also trigger a
            // "SyncWorker" here to pull down the user's full profile from Supabase
            // (e.g., current XP, avatar URL) and upsert it into the UserDao.
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return authDataSource.signOut().onSuccess {
            // CRITICAL: When the user logs out, we must wipe the local database
            // so the next person who logs into this device doesn't see their data.
        }
    }

    override suspend fun updateUser(newPassword: String): Result<Unit> {
        return authDataSource.updateUser(newPassword).onSuccess {

        }
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return authDataSource.resetPassword(email).onSuccess {

        }
    }
}