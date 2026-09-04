package com.kith.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class NetworkPost(
    val id: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("community_id")
    val communityId: String,
    val title: String,
    val content: String,
    val status: String,
    val answer: String? = null,
    val reward: Int,
    @SerialName("is_in_person")
    val isInPerson: Boolean,
    @SerialName("solver_id")
    val solverId: String? = null,
    @SerialName("created_at")
    val createdAt: Instant,
    @SerialName("solved_at")
    val solvedAt: Instant? = null,
)
