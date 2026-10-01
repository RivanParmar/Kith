package com.kith.feature.home.impl.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import com.kith.core.designsystem.theme.KithExtendedColors
import com.kith.core.designsystem.theme.KithTheme
import com.kith.core.model.data.StreakState
import kotlinx.coroutines.delay

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
    val extendedColors = KithTheme.extendedColors // Pull the theme colors

    val rewardAmount = if (streakState.isBonusDay) 10 else 5

    var startEntrance by remember { mutableStateOf(false) }
    var visibleDays by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        startEntrance = true
        for (i in 1..7) {
            delay(60)
            visibleDays = i
        }
    }

    val targetAngle = (streakState.currentUIRewardDay / 7f) * 360f
    val animatedProgressAngle by animateFloatAsState(
        targetValue = if (startEntrance) targetAngle else 0f,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "progressAngle"
    )

    val centerContentScale by animateFloatAsState(
        targetValue = if (startEntrance) 1f else 0.5f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "centerContentScale"
    )

    Surface(modifier = modifier.fillMaxSize(), color = extendedColors.dailyRewardBg) {
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
                        fontWeight = FontWeight.Bold,
                        color = extendedColors.dailyRewardTextPrimary,
                        lineHeight = 64.sp
                    )
                )
                IconButton(onClick = onDismiss, modifier = Modifier.padding(top = 8.dp)) {
                    Icon(
                        imageVector = KithIcons.Close,
                        contentDescription = "Close",
                        tint = extendedColors.dailyRewardTextPrimary,
                        modifier = Modifier.size(36.dp)
                    )
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
                        color = extendedColors.dailyRewardRingTrack,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = extendedColors.dailyRewardAccent,
                        startAngle = -90f,
                        sweepAngle = animatedProgressAngle,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize(0.95f)
                        .clip(CircleShape)
                        .background(extendedColors.dailyRewardCenterCircle),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.scale(centerContentScale)
                    ) {
                        Text(
                            text = "+$rewardAmount XP",
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = extendedColors.dailyRewardTextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (streakState.isBonusDay) "DAY 7 BONUS!" else "EXP EARNED",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (streakState.isBonusDay) Color(0xFFF59E0B) else extendedColors.dailyRewardTextSecondary,
                                letterSpacing = 2.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "YOUR WEEKLY STREAK",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = extendedColors.dailyRewardTextSecondary,
                        letterSpacing = 1.5.sp
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val activeUiDay = streakState.currentUIRewardDay
                    for (day in 1..7) {
                        val isCompleted =
                            day < activeUiDay || (day == activeUiDay && streakState.isClaimedToday)
                        val isCurrent = day == activeUiDay && !streakState.isClaimedToday

                        AnimatedVisibility(
                            visible = day <= visibleDays,
                            enter = scaleIn(
                                spring(
                                    dampingRatio = 0.5f,
                                    stiffness = Spring.StiffnessMedium
                                )
                            ) + fadeIn(),
                            modifier = Modifier.weight(1f, fill = false),
                        ) {
                            DayIndicator(
                                day = day,
                                isCompleted = isCompleted,
                                isCurrent = isCurrent,
                                extendedColors = extendedColors, // PASSED DOWN
                                showDoubleBadge = day == 7
                            )
                        }

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
                    containerColor = extendedColors.dailyRewardButtonBg,
                    contentColor = extendedColors.dailyRewardButtonText,
                    disabledContainerColor = extendedColors.dailyRewardButtonBg.copy(alpha = 0.5f),
                    disabledContentColor = extendedColors.dailyRewardButtonText.copy(alpha = 0.5f)
                )
            ) {
                AnimatedContent(
                    targetState = streakState.isClaimedToday to isClaiming,
                    transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
                    label = "buttonStateAnimation"
                ) { (claimed, claiming) ->
                    if (claiming) {
                        CircularProgressIndicator(
                            color = extendedColors.dailyRewardButtonText,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 3.dp
                        )
                    } else {
                        Text(
                            text = if (claimed) "COME BACK TOMORROW" else "CLAIM REWARD",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            )
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun DayIndicator(
    day: Int,
    isCompleted: Boolean,
    isCurrent: Boolean,
    extendedColors: KithExtendedColors,
    showDoubleBadge: Boolean
) {
    val isFuture = !isCompleted && !isCurrent

    val circleBg = if (isCompleted || isCurrent) extendedColors.dailyRewardAccent else extendedColors.dailyRewardRingTrack
    val iconTint = if (isCompleted || isCurrent) extendedColors.dailyRewardButtonText else extendedColors.dailyRewardIconFuture

    // Subtle pulsing scale for the "current" uncompleted day
    val pulseScale by animateFloatAsState(
        targetValue = if (isCurrent) 1.15f else 1f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = Spring.StiffnessMediumLow),
        label = "dayPulse"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.scale(pulseScale)
    ) {
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
                    isCompleted -> Icon(
                        imageVector = KithIcons.Check,
                        contentDescription = "Completed",
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                    showDoubleBadge -> Icon(
                        imageVector = KithIcons.StarRate,
                        contentDescription = "Bonus Day",
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            if (showDoubleBadge) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-4).dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(extendedColors.dailyRewardButtonBg)
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "2X",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = extendedColors.dailyRewardButtonText,
                            fontSize = 8.sp
                        )
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "D$day",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                color = if (isFuture) extendedColors.dailyRewardTextPrimary.copy(alpha = 0.5f) else extendedColors.dailyRewardTextPrimary
            )
        )
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