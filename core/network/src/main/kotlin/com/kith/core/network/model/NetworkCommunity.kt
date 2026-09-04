package com.kith.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class NetworkCommunity(
    val id: String,
    val name: String,
    val description: String? = null,
    val password: String,
    @SerialName("image_url")
    val imageUrl: String? = null,
    @SerialName("user_id")
    val userId: String,
    @SerialName("updated_at")
    val updatedAt: Instant,
)
