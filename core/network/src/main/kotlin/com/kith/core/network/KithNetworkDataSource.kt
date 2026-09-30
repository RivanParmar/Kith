package com.kith.core.network

import com.kith.core.network.model.NetworkCommunity
import com.kith.core.network.model.NetworkPost
import com.kith.core.network.model.NetworkTransaction
import com.kith.core.network.model.NetworkUser

interface KithNetworkDataSource {
    /** Fetches a single post by its ID */
    suspend fun getPostById(postId: String): NetworkPost

    /** Fetches the most recent [limit] posts, with author and community embedded. */
    suspend fun getPosts(limit: Int = 20): List<NetworkPost>

    suspend fun createPost(networkPost: NetworkPost)

    /** Fetches a single user row — used for the Wallet card once auth is wired up. */
    suspend fun getUserById(userId: String): NetworkUser

    suspend fun getCommunityById(communityId: String): NetworkCommunity

    suspend fun updateUserProfile(userId: String, name: String, bio: String, profileImageUrl: String?)

    suspend fun getTransactionsForUser(userId: String): List<NetworkTransaction>

    suspend fun submitAnswer(postId: String, answer: String, currentUserId: String)

    suspend fun acceptAnswer(postId: String)

    suspend fun deletePost(postId: String)

    /** Calls the Supabase SQL RPC function to claim the daily reward */
    suspend fun claimDailyRewardViaRpc(): Boolean
    suspend fun getTopUsers(sortByColumn: String): List<NetworkUser>

    suspend fun getCommunities(): List<NetworkCommunity>

    suspend fun joinCommunity(communityId: String, password: String): Boolean
    suspend fun createCommunity(community: NetworkCommunity): NetworkCommunity
    suspend fun getPostsForUser(userId: String): List<NetworkPost>

    suspend fun leaveCommunity(communityId: String): Boolean
    suspend fun deleteCommunity(communityId: String): Boolean
    suspend fun updateCommunityDescription(communityId: String, description: String)

    // NEW: Search for users by name
    suspend fun searchUsers(communityId: String, query: String): List<NetworkUser>

    suspend fun getJoinedCommunities(userId: String): List<NetworkCommunity>

    suspend fun rejectAnswer(postId: String)
    suspend fun rateSolution(postId: String, rating: Float)

}