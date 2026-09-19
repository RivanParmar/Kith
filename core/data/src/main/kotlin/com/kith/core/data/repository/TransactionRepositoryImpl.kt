package com.kith.core.data.repository

import com.kith.core.model.data.Transaction
import com.kith.core.network.KithAuthDataSource
import com.kith.core.network.KithNetworkDataSource
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val authDataSource: KithAuthDataSource,
    private val networkDataSource: KithNetworkDataSource,
) : TransactionRepository {

    override suspend fun getTransactions(): List<Transaction> {
        val userId = authDataSource.currentUserId() ?: return emptyList()

        // Fetch directly from Supabase
        val networkTransactions = networkDataSource.getTransactionsForUser(userId)

        // Map the network payload directly to your UI model
        return networkTransactions.map { network ->
            Transaction(
                id = network.id,
                title = network.posts?.title ?: "Unknown Transaction",
                xpAmount = network.amount,
                timestamp = network.time
            )
        }
    }
}

