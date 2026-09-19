package com.kith.core.data.model

import com.kith.core.database.model.UserEntity
import com.kith.core.network.model.NetworkUser

fun NetworkUser.asUserEntity() = UserEntity(
    id, name, profileImageUrl, bio, xp, rating, isPremium, problemsAsked, problemsSolved, updatedAt
)