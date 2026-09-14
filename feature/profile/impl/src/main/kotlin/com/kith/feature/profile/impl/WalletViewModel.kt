package com.kith.feature.wallet.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kith.core.model.data.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<WalletUiState>(WalletUiState.Loading)
    val uiState: StateFlow<WalletUiState> = _uiState.asStateFlow()

    init {
        loadWalletData()
    }

    private fun loadWalletData() {
        viewModelScope.launch {
            delay(500) // Simulate database fetch

            // Populating your exact mock data into the state
            _uiState.value = WalletUiState.Success(
                currentBalance = 1450,
                currentLevel = 3,
                nextTierXp = 2000,
                transactions = listOf(
                    Transaction(id = "1", title = "CS 101 Midterm Notes", timestamp = "Jan 25, 2024 • 3:45 PM", xpAmount = 150),
                    Transaction(id = "2", title = "Ride to Walmart", timestamp = "Jan 25, 2024 • 3:30 PM", xpAmount = -250),
                    Transaction(id = "3", title = "Ride to Walmart", timestamp = "Jan 25, 2024 • 3:30 PM", xpAmount = 250),
                    Transaction(id = "4", title = "Ride to Walmart", timestamp = "Jan 25, 2024 • 3:30 PM", xpAmount = -250),
                    Transaction(id = "5", title = "Ride to Walmart", timestamp = "Jan 25, 2024 • 3:30 PM", xpAmount = 250),
                    Transaction(id = "6", title = "Ride to Walmart", timestamp = "Jan 25, 2024 • 3:30 PM", xpAmount = 250)
                )
            )
        }
    }
}