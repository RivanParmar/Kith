package com.kith.core.datastore
import androidx.datastore.core.DataStore
import com.kith.core.model.data.DarkThemeConfig
import com.kith.core.model.data.UserData
import com.kith.data.DarkThemeConfigProto
import com.kith.data.UserPreferences
import com.kith.data.copy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class KithPreferencesDataSource @Inject constructor(
    private val userPreferences: DataStore<UserPreferences>,
) {
    val userData: Flow<UserData> = userPreferences.data
        .map { preferences ->
            UserData(
                darkThemeConfig = when (preferences.darkThemeConfig) {
                    null,
                    DarkThemeConfigProto.DARK_THEME_CONFIG_UNSPECIFIED,
                    DarkThemeConfigProto.UNRECOGNIZED,
                    DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM,
                        -> DarkThemeConfig.FOLLOW_SYSTEM
                    DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT -> DarkThemeConfig.LIGHT
                    DarkThemeConfigProto.DARK_THEME_CONFIG_DARK -> DarkThemeConfig.DARK
                },
                pushNotificationsEnabled = preferences.pushNotificationsEnabled,
                shouldHideOnboarding = preferences.hasDoneOnboarding,
                lastLoginTimestamp = preferences.lastLoginTimestamp
            )
        }

    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        userPreferences.updateData {
            it.copy {
                this.darkThemeConfig = when (darkThemeConfig) {
                    DarkThemeConfig.FOLLOW_SYSTEM ->
                        DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM
                    DarkThemeConfig.LIGHT -> DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT
                    DarkThemeConfig.DARK -> DarkThemeConfigProto.DARK_THEME_CONFIG_DARK
                }
            }
        }
    }

    suspend fun setPushNotificationsEnabled(enabled: Boolean) {
        userPreferences.updateData {
            it.copy {
                this.pushNotificationsEnabled = enabled
            }
        }
    }

    suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        userPreferences.updateData {
            it.copy {
                this.hasDoneOnboarding = shouldHideOnboarding
            }
        }
    }

    suspend fun updateLastLogin(timestampMs: Long) {
        userPreferences.updateData { currentPreferences ->
            currentPreferences.toBuilder()
                .setLastLoginTimestamp(timestampMs)
                .build()
        }
    }
}

