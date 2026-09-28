package com.kith.core.network.supabase

import com.kith.core.network.KithStorageDataSource
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import javax.inject.Inject

class SupabaseStorageDataSource @Inject constructor(
    supabaseClient: SupabaseClient,
) : KithStorageDataSource {

    private val bucket = supabaseClient.storage.from("media")

    override suspend fun uploadMedia(
        fileName: String,
        fileBytes: ByteArray,
    ): Result<Unit> {
        return try {
            bucket.upload(
                path = fileName,
                data = fileBytes,
                options = {
                    upsert = true
                }
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getPublicUrl(fileName: String): String {
        return bucket.publicUrl(fileName)
    }
}