package com.kith.core.network

import kotlinx.coroutines.flow.Flow

interface KithAuthDataSource {
    val sessionStatus: Flow<Boolean>
    fun currentUserId(): String?
    suspend fun signUp(email: String, password: String, displayName: String?): Result<Unit>
    suspend fun signIn(email: String, password: String): Result<Unit>
    suspend fun signOut(): Result<Unit>

    suspend fun updateUser(newPassword: String): Result<Unit>

    suspend fun resetPassword(email: String): Result<Unit>
}