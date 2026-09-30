package com.kith.core.data.repository

interface MediaRepository {
    suspend fun processAndUploadMedia(
        postId: String,
        role: String,
        imageUris: List<String>,
        pdfUri: String?,
        audioUri: String?
    ): Result<Unit>

    suspend fun getResolvedMediaUri(
        postId: String,
        role: String,
        index: Int,
        extension: String,
        isSolved: Boolean
    ): String?

    suspend fun uploadProfileImage(userId: String, imageUri: String): Result<String>
}