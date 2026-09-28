package com.kith.core.network

import com.kith.core.network.model.NetworkCommunity
import com.kith.core.network.model.NetworkPost
import com.kith.core.network.model.NetworkTransaction
import com.kith.core.network.model.NetworkUser

interface KithNetworkDataSource {
    /** Fetches the most recent [limit] posts, with author and community embedded. */
    suspend fun getPosts(limit: Int = 20): List<NetworkPost>

    suspend fun createPost(networkPost: NetworkPost)

    /** Fetches a single user row — used for the Wallet card once auth is wired up. */
    suspend fun getUserById(userId: String): NetworkUser

    suspend fun getCommunityById(communityId: String): NetworkCommunity

    suspend fun updateUserProfile(userId: String, name: String, bio: String, profileImageUrl: String?)

    suspend fun getTransactionsForUser(userId: String): List<NetworkTransaction>

    /** Calls the Supabase SQL RPC function to claim the daily reward */
    suspend fun claimDailyRewardViaRpc(): Boolean
    suspend fun getTopUsers(sortByColumn: String): List<NetworkUser>

    suspend fun getCommunities(): List<NetworkCommunity>

    suspend fun joinCommunity(communityId: String, password: String): Boolean

    suspend fun createCommunity(
        community: NetworkCommunity,
    ): NetworkCommunity

    suspend fun getPostsForUser(userId: String): List<NetworkPost>
}