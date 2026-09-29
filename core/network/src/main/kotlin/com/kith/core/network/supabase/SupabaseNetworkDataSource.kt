package com.kith.core.network.supabase

import com.kith.core.network.KithNetworkDataSource
import com.kith.core.network.model.NetworkCommunity
import com.kith.core.network.model.NetworkPost
import com.kith.core.network.model.NetworkTransaction
import com.kith.core.network.model.NetworkUser
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject

class SupabaseNetworkDataSource @Inject constructor(
    private val supabaseClient: SupabaseClient,
) : KithNetworkDataSource {
    override suspend fun getPostById(postId: String): NetworkPost {
        return supabaseClient.postgrest["posts"]
            .select {
                filter { eq("id", postId) }
            }
            .decodeSingle<NetworkPost>()
    }

    override suspend fun getPosts(limit: Int): List<NetworkPost> {
        return supabaseClient.postgrest["posts"].select { limit(count = limit.toLong()) }.decodeList<NetworkPost>()
    }

    override suspend fun createPost(networkPost: NetworkPost) {
        supabaseClient.postgrest["posts"].insert(networkPost)
    }

    override suspend fun getUserById(userId: String): NetworkUser {
        return supabaseClient.postgrest["users"].select { filter { eq("id", userId) } }.decodeSingle<NetworkUser>()
    }

    override suspend fun getCommunityById(communityId: String): NetworkCommunity {
        return supabaseClient.postgrest["communities"].select { filter { eq("id", communityId) } }.decodeSingle<NetworkCommunity>()
    }

    override suspend fun getCommunities(): List<NetworkCommunity> {
        return supabaseClient.postgrest["communities"].select().decodeList<NetworkCommunity>()
    }

    override suspend fun updateUserProfile(userId: String, name: String, bio: String, profileImageUrl: String?) {
        supabaseClient.postgrest["users"].update(
            {
                set("name", name)
                set("bio", bio)
                set("profile_image_url", profileImageUrl)
            }
        ) { filter { eq("id", userId) } }
    }

    override suspend fun getTransactionsForUser(userId: String): List<NetworkTransaction> {
        return supabaseClient.postgrest["transactions"]
            .select(Columns.raw("id, user_id, amount, time, posts(title)")) { filter { eq("user_id", userId) } }
            .decodeList<NetworkTransaction>()
    }

    override suspend fun claimDailyRewardViaRpc(): Boolean {
        return try {
            val result = supabaseClient.postgrest.rpc("claim_daily_reward")
            result.decodeAs<Boolean>()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun getTopUsers(sortByColumn: String): List<NetworkUser> {
        return supabaseClient.postgrest["users"]
            .select {
                order(column = sortByColumn, order = Order.DESCENDING)
                limit(count = 50)
            }
            .decodeList<NetworkUser>()
    }

    override suspend fun joinCommunity(communityId: String, password: String): Boolean {
        return supabaseClient.postgrest.rpc(
            function = "join_community",
            parameters = buildJsonObject {
                put("p_community_id", communityId)
                put("p_password", password)
            }
        ).decodeAs<Boolean>()
    }

    override suspend fun createCommunity(community: NetworkCommunity): NetworkCommunity {
        return supabaseClient.postgrest["communities"].insert(community) { select() }.decodeSingle<NetworkCommunity>()
    }

    override suspend fun getPostsForUser(userId: String): List<NetworkPost> {
        return supabaseClient.postgrest["posts"]
            .select {
                filter {
                    or {
                        NetworkPost::userId eq userId
                        NetworkPost::solverId eq userId
                    }
                }
            }
            .decodeList<NetworkPost>()
    }

    override suspend fun leaveCommunity(communityId: String): Boolean {
        return try {
            supabaseClient.postgrest.rpc(
                function = "leave_community",
                parameters = buildJsonObject { put("p_community_id", communityId) }
            ).decodeAs<Boolean>()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun deleteCommunity(communityId: String): Boolean {
        return try {
            supabaseClient.postgrest["communities"].delete { filter { eq("id", communityId) } }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun updateCommunityDescription(communityId: String, description: String) {
        supabaseClient.postgrest["communities"].update({ set("description", description) }) { filter { eq("id", communityId) } }
    }

    override suspend fun searchUsers(communityId: String, query: String): List<NetworkUser> {
        return supabaseClient.postgrest["users"]
            .select {
                filter {
                    // Case-insensitive search on the user's name
                    ilike("name", "%$query%")

                    // TODO: To strictly restrict this search to the selected community,
                    // add your schema-specific filter here. For example:
                    // eq("community_id", communityId)
                }
            }
            .decodeList<NetworkUser>()
    }

    override suspend fun submitAnswer(postId: String, answer: String) {
        supabaseClient.postgrest["posts"].update(
            {
                set("answer", answer)
            }
        ) {
            filter { eq("id", postId) }
        }
    }

    override suspend fun updatePostSolutionStatus(postId: String, isAccepted: Boolean) {
        supabaseClient.postgrest["posts"].update(
            {
                set("is_accepted", isAccepted)
            }
        ) {
            filter { eq("id", postId) }
        }
    }

    override suspend fun deletePost(postId: String) {
        supabaseClient.postgrest["posts"].delete {
            filter { eq("id", postId) }
        }
    }

    override suspend fun rateSolution(postId: String, rating: Int) {
        supabaseClient.postgrest["posts"].update(
            {
                set("rating", rating)
            }
        ) {
            filter { eq("id", postId) }
        }
    }

    override suspend fun getJoinedCommunities(userId: String): List<NetworkCommunity> {
        val response = supabaseClient.postgrest["community_members"]
            .select(columns = Columns.raw("communities(*)")) {
                filter { eq("user_id", userId) }
            }
            .decodeList<NetworkCommunityMembers>()

        return response.map { it.community }
    }
}

@Serializable
private data class NetworkCommunityMembers(
    @SerialName("communities")
    val community: NetworkCommunity
)