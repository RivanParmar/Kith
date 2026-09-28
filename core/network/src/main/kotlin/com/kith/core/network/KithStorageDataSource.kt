package com.kith.core.network

interface KithStorageDataSource {
    suspend fun uploadMedia(fileName: String, fileBytes: ByteArray): Result<Unit>

    fun getPublicUrl(fileName: String): String
}