package com.kith.core.data.model

import com.kith.core.database.model.CommunityEntity
import com.kith.core.network.model.NetworkCommunity

fun NetworkCommunity.asCommunityEntity(id: String) = CommunityEntity(
    id, name, description, imageUrl, updatedAt,false
)