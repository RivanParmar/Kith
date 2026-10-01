package com.kith.feature.paywall.impl.data

import android.app.Activity
import android.content.Context
import android.util.Log
import com.kith.feature.paywall.impl.BuildConfig
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.Offerings
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.PackageType
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.interfaces.PurchaseCallback
import com.revenuecat.purchases.interfaces.ReceiveCustomerInfoCallback
import com.revenuecat.purchases.interfaces.ReceiveOfferingsCallback
import com.revenuecat.purchases.models.StoreTransaction
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

class PurchaseCancelledException(message: String = "Purchase cancelled") : Exception(message)

const val PREMIUM_ENTITLEMENT_ID = "kith_premium"

@Singleton
class RevenueCatBillingManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    init {
        val apiKey = BuildConfig.REVENUECAT_API_KEY
        if (!Purchases.isConfigured && apiKey.isNotBlank()) {
            try {
                Purchases.configure(
                    PurchasesConfiguration.Builder(context, apiKey).build()
                )
                Log.d("RevenueCat", "RevenueCat initialized successfully.")
            } catch (e: Exception) {
                Log.e("RevenueCat", "Error initializing RevenueCat", e)
            }
        }
    }

    val isConfigured: Boolean
        get() = Purchases.isConfigured

    suspend fun getOfferings(): Result<Offerings> = suspendCancellableCoroutine { continuation ->
        if (!Purchases.isConfigured) {
            continuation.resume(
                Result.failure(
                    IllegalStateException(
                        "RevenueCat is not configured. Add REVENUECAT_API_KEY to local.properties."
                    )
                )
            )
            return@suspendCancellableCoroutine
        }

        Purchases.sharedInstance.getOfferings(
            object : ReceiveOfferingsCallback {
                override fun onReceived(offerings: Offerings) {
                    continuation.resume(Result.success(offerings))
                }

                override fun onError(error: PurchasesError) {
                    Log.e("RevenueCat", "Failed to fetch offerings: ${error.message}")
                    continuation.resume(Result.failure(Exception(error.message)))
                }
            }
        )
    }

    suspend fun purchasePackage(
        activity: Activity,
        rcPackage: Package
    ): Result<CustomerInfo> = suspendCancellableCoroutine { continuation ->
        if (!Purchases.isConfigured) {
            continuation.resume(
                Result.failure(
                    IllegalStateException(
                        "RevenueCat is not configured. Add REVENUECAT_API_KEY to local.properties."
                    )
                )
            )
            return@suspendCancellableCoroutine
        }

        val params = PurchaseParams.Builder(activity, rcPackage).build()

        Purchases.sharedInstance.purchase(
            params,
            object : PurchaseCallback {
                override fun onCompleted(storeTransaction: StoreTransaction, customerInfo: CustomerInfo) {
                    continuation.resume(Result.success(customerInfo))
                }

                override fun onError(error: PurchasesError, userCancelled: Boolean) {
                    if (userCancelled) {
                        continuation.resume(Result.failure(PurchaseCancelledException()))
                    } else {
                        Log.e("RevenueCat", "Purchase failed: ${error.message}")
                        continuation.resume(Result.failure(Exception(error.message)))
                    }
                }
            }
        )
    }

    suspend fun restorePurchases(): Result<CustomerInfo> = suspendCancellableCoroutine { continuation ->
        if (!Purchases.isConfigured) {
            continuation.resume(
                Result.failure(
                    IllegalStateException(
                        "RevenueCat is not configured. Add REVENUECAT_API_KEY to local.properties."
                    )
                )
            )
            return@suspendCancellableCoroutine
        }

        Purchases.sharedInstance.restorePurchases(
            object : ReceiveCustomerInfoCallback {
                override fun onReceived(customerInfo: CustomerInfo) {
                    continuation.resume(Result.success(customerInfo))
                }

                override fun onError(error: PurchasesError) {
                    Log.e("RevenueCat", "Restore purchases failed: ${error.message}")
                    continuation.resume(Result.failure(Exception(error.message)))
                }
            }
        )
    }

    suspend fun checkCustomerEntitlement(): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        if (!Purchases.isConfigured) {
            continuation.resume(Result.success(false))
            return@suspendCancellableCoroutine
        }

        Purchases.sharedInstance.getCustomerInfo(
            object : ReceiveCustomerInfoCallback {
                override fun onReceived(customerInfo: CustomerInfo) {
                    val isEntitled = isEntitledToPremium(customerInfo)
                    continuation.resume(Result.success(isEntitled))
                }

                override fun onError(error: PurchasesError) {
                    Log.e("RevenueCat", "Failed to retrieve customer info: ${error.message}")
                    continuation.resume(Result.failure(Exception(error.message)))
                }
            }
        )
    }

    fun isEntitledToPremium(customerInfo: CustomerInfo): Boolean {
        val premiumEntitlement = customerInfo.entitlements[PREMIUM_ENTITLEMENT_ID]
        return (premiumEntitlement != null && premiumEntitlement.isActive) ||
                customerInfo.entitlements.active.containsKey(PREMIUM_ENTITLEMENT_ID)
    }
}
