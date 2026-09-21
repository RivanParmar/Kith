package com.kith.feature.profile.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kith.core.data.repository.TransactionRepository
import com.kith.core.data.repository.WalletRepository
import com.kith.core.data.util.SyncManager
import com.kith.core.model.data.Transaction
import com.kith.core.ui.WalletUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    walletRepository: WalletRepository,
    transactionRepository: TransactionRepository,
    private val syncManager: SyncManager,
) : ViewModel() {

    val walletState: StateFlow<WalletUiState> = walletRepository.getWalletDataStream()
        .map { WalletUiState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = WalletUiState.Loading,
        )

    val uiState: StateFlow<TransactionsUiState> = transactionRepository.getTransactions()
        .map { TransactionsUiState.Success(it) }
        .catch { TransactionsUiState.Error(it.message ?: "Failed to load transactions") }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TransactionsUiState.Loading,
        )

    val isSyncing = syncManager.isSyncing
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    fun sync() {
        syncManager.requestSync()
    }
}

sealed interface TransactionsUiState {
    data object Loading : TransactionsUiState

    data class Success(
        val transactions: List<Transaction>,
    ) : TransactionsUiState

    data class Error(val message: String) : TransactionsUiState
}