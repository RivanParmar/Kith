package com.kith.core.database.model

import androidx.room.Embedded
import androidx.room.Relation
import com.kith.core.model.data.Post

data class PopulatedPostEntity(
    @Embedded
    val post: PostEntity,

    @Relation(
        parentColumn = "user_id",
        entityColumn = "id",
    )
    val author: UserEntity,

    @Relation(
        parentColumn = "community_id",
        entityColumn = "id",
    )
    val community: CommunityEntity,

    @Relation(
        parentColumn = "solver_id",
        entityColumn = "id",
    )
    val solver: UserEntity?,
)

fun PopulatedPostEntity.asExternalModel() = Post(
    id = post.id,
    title = post.title,
    content = post.content,
    reward = post.reward,
    author = author.asExternalModel(),
    community = community.asExternalModel(),
    createdAt = post.createdAt,
    isInPerson = post.isInPerson,
    status = post.status.name,
)