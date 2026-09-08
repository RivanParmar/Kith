package com.kith.core.data.repository

import com.kith.core.model.data.DarkThemeConfig
import com.kith.core.model.data.ProfileVisibility
import com.kith.core.model.data.UserData
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {
    val userData: Flow<UserData>
    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig)
    suspend fun setProfileVisibility(profileVisibility: ProfileVisibility)
    suspend fun setPushNotificationsEnabled(enabled: Boolean)
    suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean)

    suspend fun setLastLoginTimestamp(timestampMs: Long)
}