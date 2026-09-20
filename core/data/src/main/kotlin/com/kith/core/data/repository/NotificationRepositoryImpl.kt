package com.kith.core.data.repository

import com.kith.core.model.data.Notification
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor() : NotificationRepository {
    override fun getNotifications(): Flow<List<Notification>> {
        return flow { listOf<Notification>() }
    }

    override suspend fun insertNotifications(notifications: List<Notification>) {

    }

    override suspend fun markNotificationAsRead(id: String) {

    }
}