package com.kith.feature.paywall.impl

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kith.core.data.repository.AuthRepository
import com.kith.core.data.repository.UserRepository
import com.kith.core.database.dao.UserDao
import com.kith.core.network.KithNetworkDataSource
import com.kith.feature.paywall.impl.data.PurchaseCancelledException
import com.kith.feature.paywall.impl.data.RevenueCatBillingManager
import com.revenuecat.purchases.PackageType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaywallViewModel @Inject constructor(
    private val billingManager: RevenueCatBillingManager,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository, // Added for User ID
    private val networkDataSource: KithNetworkDataSource, // Added for Supabase Sync
    private val userDao: UserDao,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaywallUiState())
    val uiState: StateFlow<PaywallUiState> = _uiState.asStateFlow()

    init {
        loadOfferings()
        checkEntitlement()
    }

    fun onEvent(event: PaywallUiEvent) {
        when (event) {
            is PaywallUiEvent.SelectCycle -> selectCycle(event.cycle)
            is PaywallUiEvent.Purchase -> purchase(event.activity)
            is PaywallUiEvent.Restore -> restorePurchases()
            is PaywallUiEvent.DismissMessage -> _uiState.update { it.copy(userMessage = null) }
        }
    }

    private fun selectCycle(cycle: BillingCycle) {
        _uiState.update { it.copy(selectedCycle = cycle) }
    }

    private fun loadOfferings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = billingManager.getOfferings()

            result.onSuccess { offerings ->
                val currentOffering = offerings.current
                if (currentOffering != null) {
                    val monthly = currentOffering.monthly
                        ?: currentOffering.availablePackages.firstOrNull {
                            it.packageType == PackageType.MONTHLY || it.identifier.contains("monthly", true)
                        }

                    val annual = currentOffering.annual
                        ?: currentOffering.availablePackages.firstOrNull {
                            it.packageType == PackageType.ANNUAL || it.identifier.contains("annual", true)
                        }

                    _uiState.update { current ->
                        current.copy(
                            isLoading = false,
                            monthlyPackage = monthly,
                            annualPackage = annual,
                            monthlyPriceText = monthly?.product?.price?.formatted?.let { "$it / month (billed monthly)" }
                                ?: current.monthlyPriceText,
                            annualPriceText = annual?.product?.price?.formatted?.let { "$it / year (billed annually)" }
                                ?: current.annualPriceText,
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }.onFailure { error ->
                Log.w("PaywallViewModel", "Failed to load offerings from RevenueCat: ${error.message}")
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun checkEntitlement() {
        viewModelScope.launch {
            billingManager.checkCustomerEntitlement()
                .onSuccess { isEntitled ->
                    if (isEntitled) {
                        markUserAsPremium()
                        _uiState.update { it.copy(isPurchased = true) }
                    }
                }
        }
    }

    private fun purchase(activity: Activity) {
        val selectedPkg = _uiState.value.selectedPackage

        if (selectedPkg == null) {
            if (!billingManager.isConfigured) {
                _uiState.update {
                    it.copy(
                        userMessage = "RevenueCat is not configured. Add REVENUECAT_API_KEY to local.properties."
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        userMessage = "Selected subscription package is currently unavailable. Please try again shortly."
                    )
                }
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isPurchasing = true) }

            val result = billingManager.purchasePackage(activity, selectedPkg)

            result.onSuccess { customerInfo ->
                val isEntitled = billingManager.isEntitledToPremium(customerInfo)
                if (isEntitled) {
                    markUserAsPremium()
                    _uiState.update {
                        it.copy(
                            isPurchasing = false,
                            isPurchased = true,
                            userMessage = "Welcome to Kith Premium!"
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isPurchasing = false,
                            userMessage = "Purchase completed, but entitlement is pending verification."
                        )
                    }
                }
            }.onFailure { error ->
                _uiState.update { current ->
                    if (error is PurchaseCancelledException) {
                        current.copy(isPurchasing = false)
                    } else {
                        current.copy(
                            isPurchasing = false,
                            userMessage = error.message ?: "Failed to complete purchase."
                        )
                    }
                }
            }
        }
    }

    private fun restorePurchases() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRestoring = true) }

            val result = billingManager.restorePurchases()

            result.onSuccess { customerInfo ->
                val isEntitled = billingManager.isEntitledToPremium(customerInfo)
                if (isEntitled) {
                    markUserAsPremium()
                    _uiState.update {
                        it.copy(
                            isRestoring = false,
                            isPurchased = true,
                            userMessage = "Purchases restored! Kith Premium is active."
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isRestoring = false,
                            userMessage = "No active subscription found to restore."
                        )
                    }
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isRestoring = false,
                        userMessage = error.message ?: "Failed to restore purchases."
                    )
                }
            }
        }
    }

    private suspend fun markUserAsPremium() {
        try {
            // 1. Update Local UI State Instantly
            val userProfile = userRepository.getUserProfileStream().firstOrNull()
            if (userProfile != null) {
                val currentEntity = userDao.getUserStream(userProfile.id).firstOrNull()
                if (currentEntity != null) {
                    userDao.upsertUser(currentEntity.copy(isPremium = true))
                }
            }

            // 2. Update Supabase Backend Truth
            val userId = authRepository.currentUserId()
            if (userId != null) {
                // Ensure this function exists in your KithNetworkDataSource interface
                networkDataSource.updateUserPremiumStatus(userId, true)
            }
        } catch (e: Exception) {
            Log.e("PaywallViewModel", "Failed to update local user premium flag", e)
        }
    }
}