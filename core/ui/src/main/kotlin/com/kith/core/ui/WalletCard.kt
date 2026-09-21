package com.kith.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kith.core.designsystem.component.LoadingWheel
import com.kith.core.model.data.WalletData
import java.text.NumberFormat
import java.util.Locale

@Composable
fun WalletCard(
    walletUiState: WalletUiState,
    modifier: Modifier = Modifier
) {
    when (walletUiState) {
        WalletUiState.Loading -> {
            LoadingState()
        }
        is WalletUiState.Success -> {
            val gradientStart = Color(0xFF2563EB).copy(alpha = 0.9f)
            val gradientEnd = Color(0xFFB7C6E5)

            val numberFormat = NumberFormat.getNumberInstance(Locale.US)

            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(gradientStart, gradientEnd)
                        )
                    )
                    .padding(24.dp),
            ) {
                Column {
                    Text(
                        text = "Current Balance",
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = numberFormat.format(walletUiState.walletData.balance),
                            color = Color.White,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-1).sp,
                            lineHeight = 40.sp,
                        )
                        Text(
                            text = " XP",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp),
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White.copy(alpha = 0.3f),
                        thickness = 1.dp,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Wallet Level ${walletUiState.walletData.level}",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                        )

                        Text(
                            text = "Next tier: ${numberFormat.format(walletUiState.walletData.nextTierXp)} XP",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
        }
    }
}

// TODO: Replace this with a custom shimmer effect modifier
@Composable
private fun LoadingState(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        LoadingWheel(
            contentDesc = "Loading wallet details",
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

sealed interface WalletUiState {
    data object Loading : WalletUiState

    data class Success(
        val walletData: WalletData,
    ) : WalletUiState
}

@Preview(showBackground = true)
@Composable
fun WalletCardPreview() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            WalletCard(
                walletUiState = WalletUiState.Success(
                    WalletData(
                        balance = 1450,
                        level = 3,
                        nextTierXp = 2000,
                    )
                ),
            )
        }
    }
}