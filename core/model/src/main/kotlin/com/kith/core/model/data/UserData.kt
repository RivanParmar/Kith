package com.kith.core.model.data

data class UserData(
    val darkThemeConfig: DarkThemeConfig,
    val profileVisibility: ProfileVisibility,
    val pushNotificationsEnabled: Boolean,
    val shouldHideOnboarding: Boolean,
    val lastLoginTimestamp: Long
)