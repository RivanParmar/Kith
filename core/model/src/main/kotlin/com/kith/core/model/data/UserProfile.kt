package com.kith.core.model.data

data class UserProfile(
    val id: String,
    val name: String,
    val profileImageUrl: String?,
    val bio: String?,
    val xp: Int,
    val rating: Float,
    val problemsAsked: Int,
    val problemsSolved: Int,
    val isPremium: Boolean,
)