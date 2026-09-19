package com.kith.core.data.repository

import com.kith.core.model.data.Transaction

interface TransactionRepository {
    suspend fun getTransactions(): List<Transaction>
}

