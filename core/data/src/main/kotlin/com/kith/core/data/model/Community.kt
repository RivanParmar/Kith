package com.kith.core.data.model

import com.kith.core.database.model.CommunityEntity
import com.kith.core.model.data.Community
import com.kith.core.network.model.NetworkCommunity

fun NetworkCommunity.asCommunityEntity() = CommunityEntity(
    id = id,
    name = name,
    description = description,
    imageUrl = imageUrl,
    updatedAt = updatedAt,
    isJoinedByMe = false,
)

fun NetworkCommunity.asExternalModel() = Community(
    id = id,
    name = name,
    imageUrl = imageUrl,
)