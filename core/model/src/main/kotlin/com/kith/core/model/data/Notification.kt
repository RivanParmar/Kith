package com.kith.core.model.data

import kotlin.time.Instant

data class Notification(
    val id: String,
    val title: String,
    val body: String,
    val postId: String,
    val timestamp: Instant,
    val isRead: Boolean,
)
