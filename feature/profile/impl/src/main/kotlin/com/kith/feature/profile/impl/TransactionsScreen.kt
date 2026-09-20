package com.kith.feature.profile.impl

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kith.core.designsystem.component.KithMediumTopAppBar
import com.kith.core.designsystem.component.LoadingWheel
import com.kith.core.designsystem.theme.KithTheme
import com.kith.core.model.data.Transaction
import com.kith.core.ui.WalletCard
import com.kith.core.ui.WalletUiState
import com.kith.feature.profile.api.R
import com.kith.feature.profile.impl.ui.TransactionCard

@Composable
fun TransactionsScreen(
    modifier: Modifier = Modifier,
    viewModel: TransactionsViewModel = hiltViewModel(),
) {
    val walletUiState by viewModel.walletUiState.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TransactionsScreen(
        walletUiState = walletUiState,
        uiState = uiState,
        modifier = modifier,
    )
}

@Composable
internal fun TransactionsScreen(
    walletUiState: WalletUiState,
    uiState: TransactionsUiState,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        TransactionsUiState.Loading -> {
            LoadingState(modifier = modifier)
        }

        is TransactionsUiState.Error -> {
            ErrorState(error = uiState.message, modifier = modifier)
        }

        is TransactionsUiState.Success -> {
            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

            Scaffold(
                modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    KithMediumTopAppBar(
                        titleRes = R.string.feature_profile_api_transactions_title,
                        navigationIcon = null,
                        navigationIconContentDescription = null,
                        actionIcon = null,
                        actionIconContentDescription = null,
                        scrollBehavior = scrollBehavior,
                    )
                }
            ) { padding ->
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(padding),
                ) {
//                    Text(
//                        text = "Wallet",
//                        fontSize = 32.sp,
//                        fontWeight = FontWeight.Bold,
//                        color = Color(0xFF0F172A),
//                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
//                    )

                    WalletCard(
                        walletUiState = walletUiState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 24.dp),
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Transaction History",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                        )
                    }

                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(
                            items = uiState.transactions,
                            key = { it.id },
                        ) { transaction ->
                            TransactionCard(transaction = transaction)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingState(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        LoadingWheel(
            contentDesc = "Loading profile",
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Composable
private fun ErrorState(
    error: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Text(
            text = error,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionsScreenPreview() {
    val mockTransactions = listOf(
        Transaction(id = "1", title = "CS 101 Midterm Notes", timestamp = "Jan 25, 2024 • 3:45 PM", xpAmount = 150),
        Transaction(id = "2", title = "Ride to Walmart", timestamp = "Jan 25, 2024 • 3:30 PM", xpAmount = -250)
    )

    KithTheme {
        TransactionsScreen(
            walletUiState = WalletUiState.Success(
                currentBalance = 1450,
                currentLevel = 3,
                nextTierXp = 2000,
            ),
            uiState = TransactionsUiState.Success(
                transactions = mockTransactions,
            )
        )
    }
}