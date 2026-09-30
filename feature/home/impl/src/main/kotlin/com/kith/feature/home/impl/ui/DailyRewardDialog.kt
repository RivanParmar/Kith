package com.kith.feature.home.impl.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kith.core.designsystem.icon.KithIcons
import com.kith.core.designsystem.theme.KithTheme
import com.kith.core.model.data.StreakState

@Composable
fun DailyRewardDialog(
    onDismiss: () -> Unit,
    viewModel: DailyRewardDialogViewModel = hiltViewModel()
) {
    val streakState by viewModel.streakState.collectAsStateWithLifecycle()
    val isClaiming by viewModel.isClaiming.collectAsStateWithLifecycle()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        DailyRewardScreen(
            streakState = streakState,
            isClaiming = isClaiming,
            onDismiss = onDismiss,
            onClaim = {
                viewModel.claimDailyReward(onSuccess = onDismiss)
            }
        )
    }
}

@Composable
internal fun DailyRewardScreen(
    streakState: StreakState,
    isClaiming: Boolean,
    onDismiss: () -> Unit,
    onClaim: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    val bgColor = if (isDark) Color(0xFF0F172A) else Color(0xFF3B82F6)
    val primaryAccent = if (isDark) Color(0xFF00E5FF) else Color(0xFF0F172A)
    val mainText = if (isDark) Color.White else Color(0xFF0F172A)
    val secondaryText = if (isDark) Color(0xFF94A3B8) else Color(0xFF0F172A).copy(alpha = 0.7f)

    val centerCircleBg = if (isDark) Color(0xFF0F172A) else Color.White
    val ringTrackColor = if (isDark) Color(0xFF1E293B) else Color.White.copy(alpha = 0.3f)

    val buttonBg = if (isDark) Color.White else Color(0xFF0F172A)
    val buttonText = if (isDark) Color(0xFF0F172A) else Color.White

    // Visual amount purely for the UI display
    val rewardAmount = if (streakState.isBonusDay) 10 else 5

    Surface(modifier = modifier.fillMaxSize(), color = bgColor) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "Daily\nReward.",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Bold, color = mainText, lineHeight = 64.sp
                    )
                )
                IconButton(onClick = onDismiss, modifier = Modifier.padding(top = 8.dp)) {
                    Icon(imageVector = KithIcons.Add, contentDescription = "Close", tint = mainText, modifier = Modifier.size(36.dp))
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = ringTrackColor, startAngle = 0f, sweepAngle = 360f,
                        useCenter = false, style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                val progressAngle = (streakState.currentUIRewardDay / 7f) * 360f
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = primaryAccent, startAngle = -90f, sweepAngle = progressAngle,
                        useCenter = false, style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize(0.95f)
                        .clip(CircleShape)
                        .background(centerCircleBg),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "+$rewardAmount XP",
                            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold, color = mainText)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (streakState.isBonusDay) "DAY 7 BONUS!" else "EXP EARNED",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (streakState.isBonusDay) Color(0xFFF59E0B) else secondaryText,
                                letterSpacing = 2.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "YOUR WEEKLY STREAK",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = secondaryText, letterSpacing = 1.5.sp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val activeUiDay = streakState.currentUIRewardDay
                    for (day in 1..7) {
                        val isCompleted = day < activeUiDay || (day == activeUiDay && streakState.isClaimedToday)
                        val isCurrent = day == activeUiDay && !streakState.isClaimedToday
                        DayIndicator(
                            day = day, isCompleted = isCompleted, isCurrent = isCurrent,
                            isDark = isDark, primaryColor = primaryAccent, textColor = mainText,
                            showDoubleBadge = day == 7
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onClaim,
                enabled = !streakState.isClaimedToday && !isClaiming,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonBg, contentColor = buttonText,
                    disabledContainerColor = buttonBg.copy(alpha = 0.5f), disabledContentColor = buttonText.copy(alpha = 0.5f)
                )
            ) {
                if (isClaiming) {
                    CircularProgressIndicator(color = buttonText, modifier = Modifier.size(24.dp), strokeWidth = 3.dp)
                } else {
                    Text(
                        text = if (streakState.isClaimedToday) "COME BACK TOMORROW" else "CLAIM REWARD",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun DayIndicator(
    day: Int, isCompleted: Boolean, isCurrent: Boolean, isDark: Boolean,
    primaryColor: Color, textColor: Color, showDoubleBadge: Boolean
) {
    val isFuture = !isCompleted && !isCurrent
    val circleBg = if (isCompleted || isCurrent) primaryColor else if (isDark) Color(0xFF1E293B) else Color.White.copy(alpha = 0.3f)
    val iconTint = if (isCompleted || isCurrent) (if (isDark) Color(0xFF0F172A) else Color.White) else (if (isDark) Color.White.copy(alpha = 0.3f) else Color(0xFF0F172A).copy(alpha = 0.3f))

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(44.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.BottomStart)
                    .clip(CircleShape)
                    .background(circleBg),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isCompleted -> Icon(imageVector = KithIcons.Search, contentDescription = "Completed", tint = iconTint, modifier = Modifier.size(24.dp))
                    showDoubleBadge -> Icon(imageVector = KithIcons.StarRate, contentDescription = "Bonus Day", tint = iconTint, modifier = Modifier.size(20.dp))
                }
            }
            if (showDoubleBadge) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-4).dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isDark) Color.White else Color(0xFF0F172A))
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "2X", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = if (isDark) Color(0xFF0F172A) else Color.White, fontSize = 8.sp))
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "D$day", style = MaterialTheme.typography.labelMedium.copy(fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium, color = if (isFuture) textColor.copy(alpha = 0.5f) else textColor))
    }
}

@Preview
@Composable
private fun DailyRewardPreview() {
    KithTheme {
        DailyRewardScreen(
            streakState = StreakState(2, false),
            isClaiming = false,
            {},
            {}
        )
    }
}