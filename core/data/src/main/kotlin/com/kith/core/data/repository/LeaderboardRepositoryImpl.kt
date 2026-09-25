package com.kith.core.data.repository

import com.kith.core.model.data.UserProfile
import com.kith.core.network.KithNetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LeaderboardRepositoryImpl @Inject constructor(
    private val networkDataSource: KithNetworkDataSource,
) : LeaderboardRepository {

    override fun getTopUsers(sortByColumn: String): Flow<List<UserProfile>> = flow {
        try {
            val networkUsers = networkDataSource.getTopUsers(sortByColumn)

            val domainUsers = networkUsers.map { user ->
                UserProfile(
                    id = user.id,
                    name = user.name,
                    profileImageUrl = user.profileImageUrl,
                    bio = user.bio,
                    xp = user.xp,
                    rating = user.rating,
                    problemsAsked = user.problemsAsked,
                    problemsSolved = user.problemsSolved,
                    isPremium = user.isPremium,
                )
            }

            emit(domainUsers)
        } catch (_: Exception) {
            emit(emptyList())
        }
    }

}