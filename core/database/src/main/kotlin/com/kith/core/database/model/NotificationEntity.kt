package com.kith.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kith.core.model.data.Notification
import kotlin.time.Clock
import kotlin.time.Instant

@Entity(
    tableName = "notifications"
)
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val body: String,
    @ColumnInfo(name = "post_id")
    val postId: String,
    val timestamp: Instant = Clock.System.now(),
    @ColumnInfo(name = "is_read")
    val isRead: Boolean,
)

fun NotificationEntity.asExternalModel() = Notification(
    id = id.toString(),
    title = title,
    body = body,
    postId = postId,
    timestamp = timestamp,
    isRead = isRead,
)