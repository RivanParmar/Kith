package com.kith.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kith.core.database.util.PostStatus
import com.kith.core.database.util.SyncStatus
import kotlin.time.Instant

@Entity(
    tableName = "posts",
)
data class PostEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "community_id")
    val communityId: String,
    val title: String,
    val content: String,
    val status: PostStatus,
    val reward: Int,
    @ColumnInfo(name = "is_in_person")
    val isInPerson: Boolean,
    val answer: String?,
    @ColumnInfo(name = "solver_id")
    val solverId: String?,
    @ColumnInfo(name = "created_at")
    val createdAt: Instant,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Instant,
    @ColumnInfo(name = "sync_status")
    val syncStatus: SyncStatus,
)