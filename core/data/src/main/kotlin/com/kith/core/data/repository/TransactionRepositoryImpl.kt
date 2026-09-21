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
            // Fetch directly from Supabase (allowed here because flow {} builder provides coroutine context)
            val networkTransactions = networkDataSource.getTransactionsForUser(userId)

            // Map the network payload directly to your UI model
            val domainTransactions = networkTransactions.map { network ->
                Transaction(
                    id = network.id,
                    title = network.posts?.title ?: "Unknown Transaction",
                    xpAmount = network.amount,
                    timestamp = network.time
                )
            }

            // Emit the final list to the UI
            emit(domainTransactions)

        } catch (e: Exception) {
            // If the network fails, emit an empty list (or handle the error state)
            emit(emptyList())
        }
    }
}