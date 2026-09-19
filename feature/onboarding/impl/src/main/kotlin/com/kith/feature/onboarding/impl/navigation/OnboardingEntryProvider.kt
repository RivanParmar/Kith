package com.kith.feature.onboarding.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.kith.core.navigation.Navigator
import com.kith.feature.home.api.navigation.HomeNavKey
import com.kith.feature.onboarding.api.navigation.OnboardingNavKey
import com.kith.feature.onboarding.impl.OnboardingScreen

fun EntryProviderScope<NavKey>.onboardingEntry(navigator: Navigator) {
    entry<OnboardingNavKey> {
        OnboardingScreen(
            navigateToHome = { navigator.navigate(HomeNavKey) }
        )
    }
}