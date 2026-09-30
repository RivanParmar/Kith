package com.kith.core.data.repository

import com.kith.core.data.Syncable
import com.kith.core.model.data.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserRepository : Syncable {
     fun getUserProfileStream(): Flow<UserProfile?>
     suspend fun syncCurrentUser()
     suspend fun updateProfile(name: String, bio: String, profileImageUrl: String?)
     suspend fun syncFcmToken(token: String)
}