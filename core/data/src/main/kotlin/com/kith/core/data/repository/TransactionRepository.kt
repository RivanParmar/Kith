package com.kith.core.data.repository

import com.kith.core.model.data.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getTransactions(): Flow<List<Transaction>>
}

