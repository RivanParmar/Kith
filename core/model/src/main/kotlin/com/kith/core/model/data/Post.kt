package com.kith.core.model.data

import kotlin.time.Instant

data class Post(
    val id: String,
    val title: String,
    val content: String,
    val reward: Int,
    val author: User,
    val community: Community,
    val createdAt: Instant,
)