package com.kith.core.model.data

data class Community(
    val id: String,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val creatorId: String,
    val memberCount: Int = 0,
)