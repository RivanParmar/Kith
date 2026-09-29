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
    @ColumnInfo(name = "user_image_count", defaultValue = "0")
    val userImageCount: Int,
    @ColumnInfo(name = "user_has_pdf", defaultValue = "false")
    val userHasPdf: Boolean,
    @ColumnInfo(name = "user_has_audio", defaultValue = "false")
    val userHasAudio: Boolean,
    val answer: String?,
    @ColumnInfo(name = "solver_id")
    val solverId: String?,
    @ColumnInfo(name = "solver_image_count", defaultValue = "0")
    val solverImageCount: Int,
    @ColumnInfo(name = "solver_has_pdf", defaultValue = "false")
    val solverHasPdf: Boolean,
    @ColumnInfo(name = "solver_has_audio", defaultValue = "false")
    val solverHasAudio: Boolean,
    @ColumnInfo(name = "created_at")
    val createdAt: Instant,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Instant,
    @ColumnInfo(name = "sync_status")
    val syncStatus: SyncStatus,
)