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
    @SerialName("user_image_count")
    val userImageCount: Int,
    @SerialName("user_has_pdf")
    val userHasPdf: Boolean,
    @SerialName("user_has_audio")
    val userHasAudio: Boolean,
    val answer: String? = null,
    val reward: Int,
    @SerialName("is_in_person")
    val isInPerson: Boolean,
    @SerialName("solver_id")
    val solverId: String? = null,
    @SerialName("solver_image_count")
    val solverImageCount: Int,
    @SerialName("solver_has_pdf")
    val solverHasPdf: Boolean,
    @SerialName("solver_has_audio")
    val solverHasAudio: Boolean,
    @SerialName("created_at")
    val createdAt: Instant,
    @SerialName("updated_at")
    val updatedAt: Instant,
    @SerialName("solved_at")
    val solvedAt: Instant? = null,
)
