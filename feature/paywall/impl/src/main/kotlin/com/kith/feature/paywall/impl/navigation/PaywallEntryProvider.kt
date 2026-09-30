package com.kith.feature.paywall.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.kith.core.navigation.Navigator
import com.kith.feature.paywall.api.navigation.PaywallNavKey
import com.kith.feature.paywall.impl.PaywallScreen

fun EntryProviderScope<NavKey>.paywallEntry(navigator: Navigator) {
    entry<PaywallNavKey> {
        PaywallScreen(
            onBackClick = { navigator.goBack() }
        )
    }
}
