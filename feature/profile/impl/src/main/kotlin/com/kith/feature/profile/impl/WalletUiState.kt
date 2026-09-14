package com.kith.feature.wallet.impl

import com.kith.core.model.data.Transaction

sealed interface WalletUiState {
    data object Loading : WalletUiState

    data class Success(
        val currentBalance: Int,
        val currentLevel: Int,
        val nextTierXp: Int,
        val transactions: List<Transaction>
    ) : WalletUiState

    data class Error(val message: String) : WalletUiState
}

