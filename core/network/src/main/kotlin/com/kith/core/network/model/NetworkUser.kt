package com.kith.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class NetworkUser(
    val id: String,
    val name: String,
    @SerialName("profile_image_url")
    val profileImageUrl: String? = null,
    val bio: String? = null,
    val xp: Int,
    val rating: Float,
    @SerialName("is_premium")
    val isPremium: Boolean,
    @SerialName("problems_asked")
    val problemsAsked: Int,
    @SerialName("problems_solved")
    val problemsSolved: Int,
    @SerialName("created_at")
    val createdAt: Instant,
    @SerialName("updated_at")
    val updatedAt: Instant,
)
