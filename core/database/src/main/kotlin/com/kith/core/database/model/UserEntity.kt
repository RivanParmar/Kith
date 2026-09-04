package com.kith.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kith.core.model.data.User
import kotlin.time.Instant

@Entity(
    tableName = "users"
)
data class UserEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    @ColumnInfo(name = "profile_image_url")
    val profileImageUrl: String?,
    val bio: String?,
    val xp: Int,
    val rating: Float,
    @ColumnInfo(name = "is_premium")
    val isPremium: Boolean,
    @ColumnInfo(name = "problems_asked")
    val problemsAsked: Int,
    @ColumnInfo(name = "problems_solved")
    val problemsSolved: Int,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Instant,
)

fun UserEntity.asExternalModel() = User(
    id = id,
    name = name,
    profileImageUrl = profileImageUrl,
    isPremium = isPremium,
)