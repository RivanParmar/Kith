package com.kith.core.network

interface KithStorageDataSource {
    suspend fun uploadMedia(fileName: String, fileBytes: ByteArray, bucketName: String): Result<Unit>
    fun getPublicUrl(fileName: String, bucketName: String): String

    suspend fun getSignedUrl(fileName: String): Result<String>
}