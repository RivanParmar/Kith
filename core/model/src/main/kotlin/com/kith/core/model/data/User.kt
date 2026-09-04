package com.kith.core.model.data

data class User(
    val id: String,
    val name : String,
    val profileImageUrl: String?,
    val isPremium: Boolean,
)