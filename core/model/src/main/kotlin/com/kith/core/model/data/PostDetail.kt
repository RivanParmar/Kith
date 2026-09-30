package com.kith.core.model.data

import kotlin.time.Instant

data class PostDetail(
    val id: String,
    val title: String,
    val content: String,
    val reward: Int,
    val author: User,
    val community: Community,
    val createdAt: Instant,
    val isInPerson: Boolean,
    val isAccepted: Boolean,
    val answer: String?,
    val solver: User?,
    val userImageCount: Int,
    val userHasPdf: Boolean,
    val userHasAudio: Boolean,
    val solverImageCount: Int,
    val solverHasPdf: Boolean,
    val solverHasAudio: Boolean,
)