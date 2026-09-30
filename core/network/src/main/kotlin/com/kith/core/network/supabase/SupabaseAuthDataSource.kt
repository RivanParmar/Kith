package com.kith.core.network.supabase

import android.util.Log
import com.kith.core.network.KithAuthDataSource
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject

class SupabaseAuthDataSource @Inject constructor(
    private val supabase: SupabaseClient
) : KithAuthDataSource {

    override val sessionStatus: Flow<Boolean> = supabase.auth.sessionStatus
        .filterNot { it is SessionStatus.Initializing }
        .map { it is SessionStatus.Authenticated }

    override fun currentUserId(): String? {
        return supabase.auth.currentUserOrNull()?.id
    }

    override suspend fun signUp(
        email: String,
        password: String,
        displayName: String?,
    ): Result<Unit> = runCatching {
        supabase.auth.signUpWith(Email) {
            this.email = email
            this.password = password

            // Inject the display name into the auth metadata so it is available
            // without needing an extra database call.
            if (displayName != null) {
                data = buildJsonObject {
                    put("name", displayName)
                }
            }
        }
    }

    override suspend fun signIn(email: String, password: String): Result<Unit> = runCatching {
        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    override suspend fun signOut(): Result<Unit> = runCatching {
        supabase.auth.signOut()
    }

    override suspend fun updateUser(newPassword: String): Result<Unit> = runCatching {
        supabase.auth.updateUser {
            password = newPassword
        }
    }

    override suspend fun resetPassword(email: String): Result<Unit> = runCatching {
        supabase.auth.resetPasswordForEmail(email = email)
    }

    override suspend fun syncFcmToken(token: String) {
        try {
            val currentUser = currentUserId()
            if (currentUser == null) {
                Log.e("FCM_SYNC", "Abort: currentUserId() is null!")
                return
            }

            supabase.postgrest["users"].update(
                { set("fcm_token", token) }
            ) {
                filter { eq("id", currentUser) }
            }

            Log.d("FCM_SYNC", "Successfully updated Supabase for user: $currentUser")

        } catch (e: Exception) {
            // Now you will actually see if RLS or a network issue is blocking it
            Log.e("FCM_SYNC", "Supabase update threw an exception", e)
        }
    }
}