package com.kith.core.data.repository

import com.kith.core.model.data.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserRepository {
     suspend fun getUserProfileStream(): Flow<UserProfile?>
     suspend fun syncCurrentUser()
     suspend fun updateProfile(name: String, bio: String, profileImageUrl: String?)
}