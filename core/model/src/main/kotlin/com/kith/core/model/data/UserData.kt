package com.kith.core.model.data

data class UserData(
    val darkThemeConfig: DarkThemeConfig,
    val useDynamicColor: Boolean = true,
    val pushNotificationsEnabled: Boolean,
    val shouldHideOnboarding: Boolean,
    val lastLoginTimestamp: Long
)