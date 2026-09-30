package com.kith.feature.paywall.impl

import android.app.Activity
import com.revenuecat.purchases.Package

enum class BillingCycle {
    ANNUAL,
    MONTHLY,
}

data class PaywallUiState(
    val selectedCycle: BillingCycle = BillingCycle.ANNUAL,
    val isLoading: Boolean = false,
    val isPurchasing: Boolean = false,
    val isRestoring: Boolean = false,
    val isPurchased: Boolean = false,
    val userMessage: String? = null,
    val monthlyPackage: Package? = null,
    val annualPackage: Package? = null,
    val monthlyPriceText: String = "$4 / month (billed monthly)",
    val monthlySubPriceText: String = "$42/year",
    val annualPriceText: String = "$36 / year (billed annually)",
    val annualSubPriceText: String = "$3/month",
    val features: List<String> = listOf(
        "Unlimited Requests",
        "Access To Multiple Community",
        "Dedicated premium badge on profile",
        "Preferred Solver",
        "PDF and Audio Support"
    )
) {
    val activePrimaryPrice: String
        get() = when (selectedCycle) {
            BillingCycle.ANNUAL -> annualPriceText
            BillingCycle.MONTHLY -> monthlyPriceText
        }

    val activeSubPrice: String
        get() = when (selectedCycle) {
            BillingCycle.ANNUAL -> annualSubPriceText
            BillingCycle.MONTHLY -> monthlySubPriceText
        }

    val selectedPackage: Package?
        get() = when (selectedCycle) {
            BillingCycle.ANNUAL -> annualPackage
            BillingCycle.MONTHLY -> monthlyPackage
        }
}

sealed interface PaywallUiEvent {
    data class SelectCycle(val cycle: BillingCycle) : PaywallUiEvent
    data class Purchase(val activity: Activity) : PaywallUiEvent
    data object Restore : PaywallUiEvent
    data object DismissMessage : PaywallUiEvent
}
