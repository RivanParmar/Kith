package com.kith.core.model.data

data class NewPostRequest(
    val title: String,
    val content: String,
    val communityId: String,
    val reward: Int,
    val isInPerson: Boolean,
    val imageUris: List<String>,
    val pdfUri: String?,
    val audioUri: String?,
    val status: String,
    val solverId: String?,
)
