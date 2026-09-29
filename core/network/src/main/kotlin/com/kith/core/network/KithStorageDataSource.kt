package com.kith.core.network

interface KithStorageDataSource {
    suspend fun uploadMedia(fileName: String, fileBytes: ByteArray, bucketName: String = "media"): Result<Unit>
    fun getPublicUrl(fileName: String, bucketName: String = "media"): String
}