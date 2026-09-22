package com.kith.core.data.repository

import com.kith.core.model.data.Transaction
import com.kith.core.network.KithAuthDataSource
import com.kith.core.network.KithNetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val authDataSource: KithAuthDataSource,
    private val networkDataSource: KithNetworkDataSource,
) : TransactionRepository {

    override fun getTransactions(): Flow<List<Transaction>> = flow {
        val userId = authDataSource.currentUserId()

        if (userId == null) {
            emit(emptyList())
            return@flow
        }

        try {
            val networkTransactions = networkDataSource.getTransactionsForUser(userId)

            val domainTransactions = networkTransactions.map { network ->
                // CRITICAL FIX: If post_id is null, it's a Daily Reward from our Supabase RPC
                val transactionTitle = network.posts?.title ?: "Daily Reward"

                Transaction(
                    id = network.id,
                    title = transactionTitle,
                    xpAmount = network.amount,
                    timestamp = network.time
                )
            }
            emit(domainTransactions)

        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override suspend fun claimDailyReward(): Boolean {
        return networkDataSource.claimDailyRewardViaRpc()
    }
}