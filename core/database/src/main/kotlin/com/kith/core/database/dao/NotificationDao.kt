package com.kith.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.kith.core.database.model.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Upsert
    suspend fun insert(notification: NotificationEntity): Long

    @Query("UPDATE notifications SET is_read = 1 WHERE id = :notificationId")
    suspend fun markAsRead(notificationId: Int)
}