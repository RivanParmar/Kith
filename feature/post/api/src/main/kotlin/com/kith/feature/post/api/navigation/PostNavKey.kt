package com.kith.feature.post.api.navigation

import androidx.navigation3.runtime.NavKey
import com.kith.core.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data class PostDetailNavKey(val id: String) : NavKey

fun Navigator.navigateToPostDetail(
    postId: String,
) {
    navigate(PostDetailNavKey(postId))
}

@Serializable
object CreatePostNavKey : NavKey