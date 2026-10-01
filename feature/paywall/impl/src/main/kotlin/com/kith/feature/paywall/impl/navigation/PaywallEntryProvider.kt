package com.kith.feature.paywall.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.kith.core.navigation.Navigator
import com.kith.core.navigation.verticalSlideMetadata
import com.kith.feature.paywall.api.navigation.PaywallNavKey
import com.kith.feature.paywall.impl.PaywallScreen

fun EntryProviderScope<NavKey>.paywallEntry(navigator: Navigator) {
    entry<PaywallNavKey>(
        metadata = verticalSlideMetadata()
    ) {
        PaywallScreen(
            onBackClick = { navigator.goBack() }
        )
    }
}
