package com.kith.core.data.repository

import com.kith.core.database.dao.NotificationDao
import com.kith.core.database.model.NotificationEntity
import com.kith.core.database.model.asExternalModel
import com.kith.core.model.data.Notification
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.time.Clock

class NotificationRepositoryImpl @Inject constructor(
    private val notificationDao: NotificationDao,
) : NotificationRepository {

    override fun getNotifications(): Flow<List<Notification>> {
        return notificationDao.getAllNotifications().map { entities ->
            entities.map { it.asExternalModel() }
        }
    }

    override suspend fun insertNotification(
        title: String,
        body: String,
        postId: String
    ): Notification {
        val timestamp = Clock.System.now()

        val entity = NotificationEntity(
            title = title,
            body = body,
            postId = postId,
            timestamp = timestamp,
            isRead = false,
        )

        val generatedId = notificationDao.insert(entity)

        return Notification(
            id = generatedId.toString(),
            title = title,
            body = body,
            postId = postId,
            timestamp = timestamp,
            isRead = false,
        )
    }

    override suspend fun markNotificationAsRead(id: String) {
        val notificationId = id.toLongOrNull() ?: return
        notificationDao.markAsRead(notificationId)
    }
}