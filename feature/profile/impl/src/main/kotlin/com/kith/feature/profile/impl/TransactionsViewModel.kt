package com.kith.feature.profile.impl

import androidx.lifecycle.ViewModel
import com.kith.core.model.data.Transaction
import com.kith.core.ui.WalletUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor() : ViewModel() {

    val walletUiState: StateFlow<WalletUiState> = TODO()

    val uiState: StateFlow<TransactionsUiState> = TODO()
}

sealed interface TransactionsUiState {
    data object Loading : TransactionsUiState

    data class Success(
        val transactions: List<Transaction>,
    ) : TransactionsUiState

    data class Error(val message: String) : TransactionsUiState
}