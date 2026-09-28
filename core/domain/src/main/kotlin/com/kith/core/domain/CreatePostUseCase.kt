package com.kith.core.domain

import android.util.Log
import com.kith.core.data.repository.AuthRepository
import com.kith.core.data.repository.MediaRepository
import com.kith.core.data.repository.PostRepository
import com.kith.core.model.data.NewPostRequest
import java.util.UUID
import javax.inject.Inject

class CreatePostUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val postRepository: PostRepository,
    private val mediaRepository: MediaRepository,
) {
    suspend operator fun invoke(request: NewPostRequest) {
        // 1. Fetch the ID internally
        val currentUserId = authRepository.currentUserId()
            ?: throw IllegalStateException("User session expired. Please sign in again.")

        Log.d("CREATE", "User session active!")

        val newPostId = UUID.randomUUID().toString()

        // 2. Save locally instantly
        postRepository.createDraft(newPostId, currentUserId, request)
        Log.d("CREATE", "Saved locally!")

        // 3. Process and upload files
        val isTextOnly = request.imageUris.isEmpty()
                && request.pdfUri == null
                && request.audioUri == null

        if (isTextOnly) {
            // Bypass media processing and publish text directly
            postRepository.publishPostToNetwork(newPostId)
            Log.d("CREATE", "Published to network!")
            return
        }

        // 3. Process and upload files (only runs if media exists)
        val mediaResult = mediaRepository.processAndUploadMedia(
            postId = newPostId,
            role = "user",
            imageUris = request.imageUris,
            pdfUri = request.pdfUri,
            audioUri = request.audioUri
        )

        if (mediaResult.isSuccess) {
            postRepository.publishPostToNetwork(newPostId)
            Log.d("CREATE", "Published to network!")
        } else {
            // ADD THIS to see exactly what is crashing!
            val error = mediaResult.exceptionOrNull()
            Log.e("CREATE", "Media processing failed!", error)
            error?.printStackTrace()
        }

        // 4. Publish text data if media succeeded
        if (mediaResult.isSuccess) {
            postRepository.publishPostToNetwork(newPostId)
            Log.d("CREATE", "Published to network!")
        }
    }
}