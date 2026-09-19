package com.kith.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.kith.core.database.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    // ─── Read ────────────────────────────────────────────────────────────────

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserStream(userId: String): Flow<UserEntity?>

    // ─── Write ───────────────────────────────────────────────────────────────

    @Upsert
    suspend fun upsertUser(user: UserEntity)

    // ─── Delete ──────────────────────────────────────────────────────────────

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: String)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()


    @Query(
        """
        UPDATE users
        SET name = :name,
            bio = :bio,
            profile_image_url = :profileImageUrl
        WHERE id = :userId
        """
    )

    suspend fun updateProfile(
        userId: String,
        name: String,
        bio: String,
        profileImageUrl: String?
    )
}