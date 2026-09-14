package com.kith.feature.wallet.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kith.core.designsystem.component.LoadingWheel
import com.kith.core.model.data.Transaction
import com.kith.core.ui.WalletCard
import com.kith.feature.profile.impl.TransactionCard

@Composable
fun WalletRoute(
    modifier: Modifier = Modifier,
    viewModel: WalletViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is WalletUiState.Loading -> {
            LoadingWheel(contentDesc = "Loading Wallet", modifier = modifier.fillMaxSize())
        }
        is WalletUiState.Error -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.message)
            }
        }
        is WalletUiState.Success -> {
            WalletScreen(
                currentBalance = state.currentBalance,
                currentLevel = state.currentLevel,
                nextTierXp = state.nextTierXp,
                transactions = state.transactions,
                modifier = modifier
            )
        }
    }
}

@Composable
fun WalletScreen(
    currentBalance: Int,
    currentLevel: Int,
    nextTierXp: Int,
    transactions: List<Transaction>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        Text(
            text = "Wallet",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A),
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
        )

        WalletCard(
            currentBalance = currentBalance,
            currentLevel = currentLevel,
            nextTierXp = nextTierXp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Transaction History",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = transactions,
                key = { it.id }
            ) { transaction ->
                TransactionCard(transaction = transaction)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8F9FA)
@Composable
fun WalletScreenPreview() {
    val mockTransactions = listOf(
        Transaction(id = "1", title = "CS 101 Midterm Notes", timestamp = "Jan 25, 2024 • 3:45 PM", xpAmount = 150),
        Transaction(id = "2", title = "Ride to Walmart", timestamp = "Jan 25, 2024 • 3:30 PM", xpAmount = -250)
    )

    MaterialTheme {
        WalletScreen(
            currentBalance = 1450,
            currentLevel = 3,
            nextTierXp = 2000,
            transactions = mockTransactions
        )
    }
}