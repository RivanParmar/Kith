package com.kith.core.data.repository

import com.kith.core.model.data.WalletData
import kotlinx.coroutines.flow.Flow

interface WalletRepository {
    fun getWalletDataStream(): Flow<WalletData>
}

