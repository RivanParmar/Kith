package com.kith.core.data.repository

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.kith.core.network.KithStorageDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import androidx.core.net.toUri
import java.io.ByteArrayOutputStream
import java.io.File

class OfflineFirstMediaRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val storageDataSource: KithStorageDataSource,
) : MediaRepository {

    override suspend fun processAndUploadMedia(
        postId: String,
        role: String,
        imageUris: List<String>,
        pdfUri: String?,
        audioUri: String?,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // 1. Process Images (Compress to WebP)
            imageUris.forEachIndexed { index, uriString ->
                val fileName = "kith_post_${postId}_${role}_img_$index.webp"
                val webpBytes = compressToWebP(uriString.toUri())

                // Save locally to Pictures/Kith
                saveToMediaStore(fileName, webpBytes, "image/webp", Environment.DIRECTORY_PICTURES)
                // Upload to Supabase
                storageDataSource.uploadMedia(fileName, webpBytes)
            }

            // 2. Process PDF
            pdfUri?.let { uriString ->
                val fileName = "kith_post_${postId}_${role}_doc.pdf"
                val pdfBytes = readBytesFromUri(uriString.toUri())

                // Save locally to Documents/Kith
                saveToMediaStore(
                    fileName,
                    pdfBytes,
                    "application/pdf",
                    Environment.DIRECTORY_DOCUMENTS
                )
                storageDataSource.uploadMedia(fileName, pdfBytes)
            }

            // 3. Process Audio (Assume forced to .m4a earlier in the UI)
            audioUri?.let { uriString ->
                val fileName = "kith_post_${postId}_${role}_audio.m4a"
                val audioBytes = readBytesFromUri(uriString.toUri())

                // Save locally to Music/Kith
                saveToMediaStore(fileName, audioBytes, "audio/mp4", Environment.DIRECTORY_MUSIC)
                storageDataSource.uploadMedia(fileName, audioBytes)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getResolvedMediaUri(
        postId: String,
        role: String,
        index: Int,
        extension: String,
        isSolved: Boolean
    ): String? {
        val fileName = "kith_post_${postId}_${role}_img_$index.$extension"
        val directory = when(extension) {
            "webp" -> Environment.DIRECTORY_PICTURES
            "pdf" -> Environment.DIRECTORY_DOCUMENTS
            else -> Environment.DIRECTORY_MUSIC
        }

        val localFile = File(
            Environment.getExternalStoragePublicDirectory(directory),
            "Kith/$fileName"
        )

        return when {
            localFile.exists() -> localFile.absolutePath
            !isSolved -> storageDataSource.getPublicUrl(fileName)
            else -> null
        }
    }

    private fun compressToWebP(sourceUri: Uri): ByteArray {
        val inputStream = context.contentResolver.openInputStream(sourceUri)
            ?: throw IllegalArgumentException("Cannot open URI")
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()

        val outputStream = ByteArrayOutputStream()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 80, outputStream)
        } else {
            @Suppress("DEPRECATION")
            bitmap.compress(Bitmap.CompressFormat.WEBP, 80, outputStream)
        }
        return outputStream.toByteArray()
    }

    private fun readBytesFromUri(uri: Uri): ByteArray {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Cannot open URI")
        return inputStream.use { it.readBytes() }
    }

    private fun saveToMediaStore(fileName: String, data: ByteArray, mimeType: String, directory: String) {
        val resolver = context.contentResolver

        // FIX: Route to the correct Android database table based on MimeType
        val collection = when {
            mimeType.startsWith("image/") -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            mimeType.startsWith("audio/") -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            else -> MediaStore.Files.getContentUri("external")
        }

        val relativePath = "$directory/Kith/"

        // 1. Check if the file already exists and delete it
        val projection = arrayOf(MediaStore.MediaColumns._ID)
        val selection = "${MediaStore.MediaColumns.DISPLAY_NAME} = ? AND ${MediaStore.MediaColumns.RELATIVE_PATH} = ?"
        val selectionArgs = arrayOf(fileName, relativePath)

        resolver.query(collection, projection, selection, selectionArgs, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                val existingUri = android.content.ContentUris.withAppendedId(collection, id)
                resolver.delete(existingUri, null, null)
            }
        }

        // 2. Insert the new file
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        }

        val uri = resolver.insert(collection, contentValues)

        uri?.let { destUri ->
            resolver.openOutputStream(destUri)?.use { output ->
                output.write(data)
            }
            contentValues.clear()
            contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
            resolver.update(destUri, contentValues, null, null)
        }
    }
}