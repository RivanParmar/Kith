package com.kith.core.notifications

import com.kith.core.model.data.Notification

interface Notifier {
    fun postNotifications(notification: Notification)
}