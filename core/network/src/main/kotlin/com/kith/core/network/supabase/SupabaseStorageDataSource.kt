package com.kith.core.network.supabase

import com.kith.core.network.KithStorageDataSource
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

class SupabaseStorageDataSource @Inject constructor(
    private val supabaseClient: SupabaseClient,
) : KithStorageDataSource {

    companion object {
        // Explicitly defining your buckets here prevents typos
        const val BUCKET_MEDIA = "media"
        const val BUCKET_PROFILE = "profile_image"
    }

    override suspend fun uploadMedia(
        fileName: String,
        fileBytes: ByteArray,
        bucketName: String
    ): Result<Unit> {
        return try {
            // Dynamically selects "media" or "profile_image" based on what the Repository requested
            supabaseClient.storage.from(bucketName).upload(
                path = fileName,
                data = fileBytes,
                options = {
                    upsert = true // Overwrites old profile pictures with the same name to save space
                }
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getPublicUrl(fileName: String, bucketName: String): String {
        return supabaseClient.storage.from(bucketName).publicUrl(fileName)
    }

    override suspend fun getSignedUrl(fileName: String): Result<String> {
        return try {
            val url = supabaseClient.storage.from("media").createSignedUrl(
                fileName, expiresIn = 60.seconds
            )
            Result.success(url)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}