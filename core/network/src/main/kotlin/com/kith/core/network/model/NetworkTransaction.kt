package com.kith.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTransaction(
    val id: String,
    @SerialName("user_id") val userId: String,
    val amount: Int,
    val time: String, // Stored as timestamp string in Supabase
    val posts: NetworkPostTitle? = null // Holds the joined title
)

@Serializable
data class NetworkPostTitle(
    val title: String
)

