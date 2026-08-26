package com.kith.core.model.data

import kotlin.time.Instant

data class Post(
    val id: String,
    val profile: Profile,
    val title: String,
    val content: String,
    val credits: Int,
    val postDate: Instant,
)
