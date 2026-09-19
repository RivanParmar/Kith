package com.kith.core.model.data

data class Profile(
    val name: String,
    val avatarUrl: String? = null,
    val rating: Double = 4.9
)
