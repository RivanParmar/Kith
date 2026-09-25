package com.kith.feature.home.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kith.core.data.repository.TransactionRepository
import com.kith.core.data.util.StreakCalculator
import com.kith.core.model.data.StreakState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DailyRewardDialogViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _isClaiming = MutableStateFlow(false)
    val isClaiming = _isClaiming.asStateFlow()

    val streakState: StateFlow<StreakState> = transactionRepository.getTransactions()
        .map { transactions ->
            StreakCalculator.calculate(transactions)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = StreakState()
        )

    fun claimDailyReward(onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        val currentState = streakState.value

        if (currentState.isClaimedToday || _isClaiming.value) return

        viewModelScope.launch {
            _isClaiming.value = true
            try {
                // Call Supabase RPC entirely through TransactionRepository
                val success = transactionRepository.claimDailyReward()

                if (success) {
                    onSuccess()
                } else {
                    onError("Reward has already been claimed today.")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Failed to claim daily reward")
            } finally {
                _isClaiming.value = false
            }
        }
    }
}