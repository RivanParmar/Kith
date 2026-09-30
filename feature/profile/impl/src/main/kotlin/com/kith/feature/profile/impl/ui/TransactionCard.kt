package com.kith.feature.profile.impl.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kith.core.model.data.Transaction
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import kotlin.math.abs

@Composable
internal fun TransactionCard(
    transaction: Transaction,
    modifier: Modifier = Modifier,
) {
    val isPositive = transaction.xpAmount >= 0
    val backgroundColor = if (isPositive) Color(0xFFE6F4EA) else Color(0xFFFCE8E6)
    val textColor = if (isPositive) Color(0xFF137333) else Color(0xFFC5221F)
    val sign = if (isPositive) "+" else "-"
    val displayXp = "$sign ${abs(transaction.xpAmount)} XP"

    val formattedDate = remember(transaction.timestamp) {
        dateFormatted(transaction.timestamp)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFF0F0F0)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                )
                Text(
                    text = formattedDate,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }

            Box(
                modifier = Modifier
                    .background(color = backgroundColor, shape = RoundedCornerShape(50))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = displayXp,
                    color = textColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

private fun dateFormatted(timestampStr: String): String {
    return try {
        val instant = java.time.Instant.parse(timestampStr)
        DateTimeFormatter
            .ofLocalizedDate(FormatStyle.MEDIUM)
            .withLocale(Locale.getDefault())
            .withZone(ZoneId.systemDefault())
            .format(instant)
    } catch (e: Exception) {
        timestampStr // Fallback to raw string if parsing fails
    }
}