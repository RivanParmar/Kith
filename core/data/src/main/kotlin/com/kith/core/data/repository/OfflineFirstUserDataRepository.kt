package com.kith.core.data.repository

import com.kith.core.datastore.KithPreferencesDataSource
import com.kith.core.model.data.DarkThemeConfig
import com.kith.core.model.data.ProfileVisibility
import com.kith.core.model.data.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OfflineFirstUserDataRepository @Inject constructor(
    private val kithPreferencesDataSource: KithPreferencesDataSource,
) : UserDataRepository {

    override val userData: Flow<UserData> =
        kithPreferencesDataSource.userData

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        kithPreferencesDataSource.setDarkThemeConfig(darkThemeConfig)
    }

    override suspend fun setProfileVisibility(profileVisibility: ProfileVisibility) {
        kithPreferencesDataSource.setProfileVisibility(profileVisibility)
    }

    override suspend fun setPushNotificationsEnabled(enabled: Boolean) {
        kithPreferencesDataSource.setPushNotificationsEnabled(enabled)
    }

    override suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        kithPreferencesDataSource.setShouldHideOnboarding(shouldHideOnboarding)
    }

    override suspend fun setLastLoginTimestamp(timestampMs: Long) {
        kithPreferencesDataSource.updateLastLogin(timestampMs)
    }
}