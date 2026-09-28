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
    createdAt = createdAt,
    updatedAt = updatedAt,
    status = PostStatus.valueOf(status.uppercase()),
    solverId = solverId,
    answer = answer,
    isInPerson = isInPerson,
    syncStatus = SyncStatus.SYNCED
)