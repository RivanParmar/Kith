package com.kith.core.network.supabase

import com.kith.core.network.KithNetworkDataSource
import com.kith.core.network.model.NetworkCommunity
import com.kith.core.network.model.NetworkPost
import com.kith.core.network.model.NetworkUser
import com.kith.core.network.model.NetworkTransaction
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.postgrest
import javax.inject.Inject

class SupabaseNetworkDataSource @Inject constructor(
    private val supabaseClient: SupabaseClient
) : KithNetworkDataSource {

    override suspend fun getPosts(limit: Int): List<NetworkPost> {
        return supabaseClient.postgrest["posts"]
            .select {
                limit(count = limit.toLong())
            }
            .decodeList<NetworkPost>()
    }

    override suspend fun getUserById(userId: String): NetworkUser {
        return supabaseClient.postgrest["users"]
            .select {
                filter { eq("id", userId) }
            }
            .decodeSingle<NetworkUser>()
    }

    override suspend fun getCommunityById(communityId: String): NetworkCommunity {
        return supabaseClient.postgrest["communities"]
            .select {
                filter { eq("id", communityId) }
            }
            .decodeSingle<NetworkCommunity>()
    }

    // --- Using the Supabase DSL for a partial update! ---
    override suspend fun updateUserProfile(userId: String, name: String, bio: String, profileImageUrl: String?) {
        supabaseClient.postgrest["users"].update(
            {
                set("name", name)
                set("bio", bio)
                set("profile_image_url", profileImageUrl)
            }
        ) {
            filter { eq("id", userId) }
        }
    }



    override suspend fun getTransactionsForUser(userId: String): List<NetworkTransaction> {
        return supabaseClient.postgrest["transactions"]
            // Pass the columns here as a parameter!
            .select(Columns.raw("id, user_id, amount, time, posts(title)")) {
                filter { eq("user_id", userId) }
            }
            .decodeList<NetworkTransaction>()
    }
}