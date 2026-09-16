package com.kith.core.data.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val sessionStatus: Flow<Boolean>

    suspend fun currentUserId(): String?

    suspend fun signUp(email: String, password: String, displayName: String?): Result<Unit>

    suspend fun signIn(email: String, password: String): Result<Unit>

    suspend fun signOut(): Result<Unit>

    suspend fun updateUser(newPassword: String): Result<Unit>

    suspend fun resetPassword(email: String): Result<Unit>
}