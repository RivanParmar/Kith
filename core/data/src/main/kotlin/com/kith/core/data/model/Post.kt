package com.kith.core.data.model

import com.kith.core.database.model.PostEntity
import com.kith.core.database.util.PostStatus
import com.kith.core.database.util.SyncStatus
import com.kith.core.network.model.NetworkPost

fun NetworkPost.asEntity() = PostEntity(
    id = id,
    userId = userId,
    communityId = communityId,
    title = title,
    content = content,
    reward = reward,
    userImageCount = userImageCount,
    userHasPdf = userHasPdf,
    userHasAudio = userHasAudio,
    createdAt = createdAt,
    updatedAt = updatedAt,
    status = PostStatus.valueOf(status.uppercase()),
    solverId = solverId,
    answer = answer,
    solverImageCount = solverImageCount,
    solverHasPdf = solverHasPdf,
    solverHasAudio = solverHasAudio,
    isInPerson = isInPerson,
    syncStatus = SyncStatus.SYNCED,
    rating = rating,
)

fun PostEntity.asNetworkModel() = NetworkPost(
    id = id,
    userId = userId,
    communityId = communityId,
    title = title,
    content = content,
    status = status.name.lowercase(),
    userImageCount = userImageCount,
    userHasPdf = userHasPdf,
    userHasAudio = userHasAudio,
    answer = answer,
    reward = reward,
    isInPerson = isInPerson,
    solverId = solverId,
    solverImageCount = solverImageCount,
    solverHasPdf = solverHasPdf,
    solverHasAudio = solverHasAudio,
    createdAt = createdAt,
    updatedAt = updatedAt,
    solvedAt = null,
    rating = rating,
)