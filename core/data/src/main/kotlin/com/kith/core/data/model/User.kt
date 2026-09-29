package com.kith.core.data.model

import com.kith.core.database.model.UserEntity
import com.kith.core.model.data.User
import com.kith.core.network.model.NetworkUser

fun NetworkUser.asUserEntity() = UserEntity(
    id, name, profileImageUrl, bio, xp, rating, isPremium, problemsAsked, problemsSolved, updatedAt
)

fun NetworkUser.asExternalModel() = User(
    id,name, profileImageUrl, isPremium, rating
)