package com.kith.core.database.model

import androidx.room.Embedded
import androidx.room.Relation
import com.kith.core.database.util.PostStatus
import com.kith.core.model.data.Post
import com.kith.core.model.data.PostDetail

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

fun PopulatedPostEntity.asPostDetail() = PostDetail(
    id = post.id,
    title = post.title,
    content = post.content,
    reward = post.reward,
    author = author.asExternalModel(),
    community = community.asExternalModel(),
    createdAt = post.createdAt,
    isInPerson = post.isInPerson,
    isAccepted = post.status == PostStatus.SOLVED,
    answer = post.answer,
    solver = solver?.asExternalModel(),
    userImageCount = post.userImageCount,
    userHasPdf = post.userHasPdf,
    userHasAudio = post.userHasAudio,
    solverImageCount = post.solverImageCount,
    solverHasPdf = post.solverHasPdf,
    solverHasAudio = post.solverHasAudio,
    rating = post.rating,
)