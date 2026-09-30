package com.kith.feature.home.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kith.core.data.repository.PostRepository
import com.kith.core.data.repository.TransactionRepository
import com.kith.core.data.repository.WalletRepository
import com.kith.core.data.util.StreakCalculator
import com.kith.core.data.util.SyncManager
import com.kith.core.network.KithAuthDataSource
import com.kith.core.ui.PostsFeedUiState
import com.kith.core.ui.WalletUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    postRepository: PostRepository,
    walletRepository: WalletRepository,
    transactionRepository: TransactionRepository, // ADDED THIS
    private val syncManager: SyncManager,
    authDataSource: KithAuthDataSource,
) : ViewModel() {

    init {
        syncManager.requestSync()
    }

    val currentUserId = authDataSource.currentUserId()

    // ADDED: Track if the user dismissed the dialog in this session
    private val hasDismissedDailyReward = MutableStateFlow(false)

    // ADDED: Logic to determine if the dialog should be shown
    val showDailyRewardDialog: StateFlow<Boolean> = combine(
        transactionRepository.getTransactions(),
        hasDismissedDailyReward
    ) { transactions, hasDismissed ->
        val isClaimedToday = StreakCalculator.calculate(transactions).isClaimedToday
        !isClaimedToday && !hasDismissed
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false,
    )

    val walletState: StateFlow<WalletUiState> = walletRepository.getWalletDataStream()
        .map { WalletUiState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = WalletUiState.Loading,
        )

    val feedState: StateFlow<PostsFeedUiState> = postRepository.getAllPostsStream()
        .map { PostsFeedUiState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PostsFeedUiState.Loading,
        )

    val isSyncing = syncManager.isSyncing
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    // ADDED THIS
    fun dismissDailyRewardDialog() {
        hasDismissedDailyReward.value = true
    }

    fun sync() {
        syncManager.requestSync()
    }
}