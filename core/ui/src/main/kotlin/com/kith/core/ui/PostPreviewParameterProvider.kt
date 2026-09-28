package com.kith.core.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.kith.core.model.data.Community
import com.kith.core.model.data.Post
import com.kith.core.model.data.User
import com.kith.core.ui.KithPreviewData.posts
import kotlin.time.Clock

class PostPreviewParameterProvider : PreviewParameterProvider<List<Post>> {

    override val values: Sequence<List<Post>> = sequenceOf(posts)
}

object KithPreviewData {

    val users = listOf(
        User(
            id = "u1",
            name = "Emily Watson",
            profileImageUrl = null,
            isPremium = false,
            rating = 4.9f
        ),
        User(
            id = "u2",
            name = "Brandon Miller",
            profileImageUrl = null,
            isPremium = true,
            rating = 4.7f
        ),
        User(
            id = "u3",
            name = "r link",
            profileImageUrl = null,
            isPremium = true,
            rating = 5.0f
        )

    )

    val communities = listOf(
        Community(
            id = "c1",
            name = "CS 101",
            imageUrl = null,
            description = TODO(),
            creatorId = TODO(),
            memberCount = TODO(),
        ),
        Community(
            id = "c2",
            name = "General",
            imageUrl = null,
            description = TODO(),
            creatorId = TODO(),
            memberCount = TODO()
        )
    )

    val posts = listOf(
        Post(
            id = "p1",
            title = "Need notes for CS 101 Midterm",
            content = "I missed the last two lectures. Happy to pay XP for good notes.",
            reward = 150,
            author = users[0],
            community = communities[0],
            createdAt = Clock.System.now(),
            isInPerson = false,
            status = TODO(),
        ),
        Post(
            id = "p2",
            title = "Ride to local Walmart this afternoon",
            content = "Need to grab some groceries. Will pay for the ride.",
            reward = 250,
            author = users[1],
            community = communities[1],
            createdAt = Clock.System.now(),
            isInPerson = true,
            status = TODO(),
        ),
        Post(
            id = "p3",
            title = "Ride To DMART",
            content = "want mcq pdf",
            reward = 300,
            author = users[2],
            community = communities[1],
            createdAt = Clock.System.now(),
            isInPerson = true,
            status = TODO(),
        ),

    )

    const val WALLET_BALANCE = 1450
    const val WALLET_LEVEL = 3
    const val NEXT_TIER_XP = 2000
}