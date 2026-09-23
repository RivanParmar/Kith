package com.kith.core.data.repository

import com.kith.core.model.data.UserProfile
import kotlinx.coroutines.flow.Flow

interface LeaderboardRepository {
    fun getTopUsers(sortByColumn: String) : Flow<List<UserProfile>>
}