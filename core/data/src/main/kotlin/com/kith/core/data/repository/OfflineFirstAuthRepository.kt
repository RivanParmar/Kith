package com.kith.core.data.repository

import com.kith.core.data.model.asUserEntity
import com.kith.core.database.dao.UserDao
import com.kith.core.network.KithAuthDataSource
import com.kith.core.network.KithNetworkDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OfflineFirstAuthRepository @Inject constructor(
    private val authDataSource: KithAuthDataSource,
    private val networkDataSource: KithNetworkDataSource,
    private val userDao: UserDao,
) : AuthRepository {
    override val sessionStatus: Flow<Boolean> = authDataSource.sessionStatus

    override suspend fun currentUserId(): String? {
        return authDataSource.currentUserId()
    }

    override suspend fun signUp(email: String, password: String, displayName: String?): Result<Unit> {
        return authDataSource.signUp(email, password, displayName).onSuccess {
            val userId = authDataSource.currentUserId() ?: return@onSuccess
            val user = networkDataSource.getUserById(userId)
            userDao.upsertUser(user.asUserEntity())
        }
    }

    override suspend fun signIn(email: String, password: String): Result<Unit> {
        return authDataSource.signIn(email, password).onSuccess {
            val userId = authDataSource.currentUserId() ?: return@onSuccess
            val user = networkDataSource.getUserById(userId)
            userDao.upsertUser(user.asUserEntity())
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return authDataSource.signOut().onSuccess {
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