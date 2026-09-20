package com.kith.feature.home.impl.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kith.core.designsystem.icon.KithIcons

@Composable
fun DailyRewardDialog(
    onDismiss: () -> Unit,
    onClaim: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        DailyRewardScreen(
            onDismiss = onDismiss,
            onClaim = onClaim
        )
    }
}

@Composable
fun DailyRewardScreen(
    onDismiss: () -> Unit,
    onClaim: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    // Colors exactly as described in prompt
    val bgColor = if (isDark) Color(0xFF0F172A) else Color(0xFF3B82F6) // Deep navy / Vivid blue
    val primaryAccent = if (isDark) Color(0xFF00E5FF) else Color(0xFF0F172A) // Cyan / Deep navy
    val mainText = if (isDark) Color.White else Color(0xFF0F172A)
    val secondaryText = if (isDark) Color(0xFF94A3B8) else Color(0xFF0F172A).copy(alpha = 0.7f)
    
    val centerCircleBg = if (isDark) Color(0xFF0F172A) else Color.White
    val ringTrackColor = if (isDark) Color(0xFF1E293B) else Color.White.copy(alpha = 0.3f)
    
    val buttonBg = if (isDark) Color.White else Color(0xFF0F172A)
    val buttonText = if (isDark) Color(0xFF0F172A) else Color.White

    Surface(
        modifier = modifier.fillMaxSize(),
        color = bgColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "Daily\nReward.",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = mainText,
                        lineHeight = 64.sp
                    )
                )
                
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(
                        imageVector = KithIcons.Add,
                        contentDescription = "Close",
                        tint = mainText,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Center XP Circle
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background Track
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = ringTrackColor,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                
                // Progress Arc
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = primaryAccent,
                        startAngle = -90f,
                        sweepAngle = 270f, // 4/7 approx
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Inner circle background
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.85f)
                        .clip(CircleShape)
                        .background(centerCircleBg),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "+100 XP",
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = mainText
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "EXP EARNED",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = secondaryText,
                                letterSpacing = 2.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Weekly Streak
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "YOUR WEEKLY STREAK",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = secondaryText,
                        letterSpacing = 1.5.sp
                    )
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Days 1-7
                    val currentDay = 4
                    for (day in 1..7) {
                        DayIndicator(
                            day = day,
                            isCompleted = day < currentDay,
                            isCurrent = day == currentDay,
                            isDark = isDark,
                            primaryColor = primaryAccent,
                            textColor = mainText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Claim Button
            Button(
                onClick = onClaim,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonBg,
                    contentColor = buttonText
                )
            ) {
                Text(
                    text = "CLAIM REWARD",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                )
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
    isDark: Boolean,
    primaryColor: Color,
    textColor: Color
) {
    val isFuture = !isCompleted && !isCurrent
    
    val circleBg = when {
        isCompleted || isCurrent -> primaryColor
        else -> if (isDark) Color(0xFF1E293B) else Color.White.copy(alpha = 0.3f)
    }
    
    val iconTint = when {
        isCompleted || isCurrent -> if (isDark) Color(0xFF0F172A) else Color.White
        else -> if (isDark) Color.White.copy(alpha = 0.3f) else Color(0xFF0F172A).copy(alpha = 0.3f)
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(44.dp)
        ) {
            // Main Circle
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.BottomStart)
                    .clip(CircleShape)
                    .background(circleBg),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isCompleted -> {
                        Icon(
                            imageVector = KithIcons.Search,
                            contentDescription = "Completed",
                            tint = iconTint,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    day == 7 -> {
                        Icon(
                            imageVector = KithIcons.StarRate,
                            contentDescription = "Day 7",
                            tint = iconTint,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            
            // Small 2X Badge for Day 7
            if (day == 7) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-4).dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isDark) Color.White else Color(0xFF0F172A))
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "2X",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFF0F172A) else Color.White,
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
                color = if (isFuture) textColor.copy(alpha = 0.5f) else textColor
            )
        )
    }
}
