package com.kith.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kith.core.model.data.Community
import kotlin.time.Instant

@Entity(tableName = "communities")
data class CommunityEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String?,
    @ColumnInfo(name = "image_url")
    val imageUrl: String?,
    @ColumnInfo(name = "creator_id", defaultValue = "")
    val creatorId: String,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Instant,
    @ColumnInfo(name = "is_joined_by_me")
    val isJoinedByMe: Boolean = false,
)

fun CommunityEntity.asExternalModel() = Community(
    id = id,
    name = name,
    description = description,
    imageUrl = imageUrl,
    creatorId = creatorId
)