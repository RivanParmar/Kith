package com.kith.core.data.repository

interface MediaRepository {
    suspend fun processAndUploadMedia(
        postId: String,
        role: String,
        imageUris: List<String>,
        pdfUri: String?,
        audioUri: String?
    ): Result<Unit>

    fun getResolvedMediaUri(
        postId: String,
        role: String,
        index: Int,
        extension: String,
        isSolved: Boolean
    ): String?
}