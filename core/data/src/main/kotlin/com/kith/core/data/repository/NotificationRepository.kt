package com.kith.core.data.repository

import com.kith.core.model.data.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getNotifications(): Flow<List<Notification>>
    suspend fun insertNotifications(notifications: List<Notification>)
    suspend fun markNotificationAsRead(id: String)
}
