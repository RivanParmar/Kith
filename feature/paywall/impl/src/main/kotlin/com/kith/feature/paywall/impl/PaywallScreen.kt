package com.kith.feature.paywall.impl

import android.app.Activity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kith.core.designsystem.icon.KithIcons
import com.kith.core.designsystem.theme.OutfitFontFamily
import kotlinx.coroutines.delay

private val PaywallBlue = Color(0xFF0066FF)
private val LightPillBackground = Color(0xFFF3F4F6)
private val PillBorderColor = Color(0xFFE5E7EB)
private val TextDark = Color(0xFF111827)
private val TextMuted = Color(0xFF6B7280)

@Composable
fun PaywallScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PaywallViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val activity = context as? Activity

    LaunchedEffect(uiState.userMessage) {
        val msg = uiState.userMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.onEvent(PaywallUiEvent.DismissMessage)
        }
    }

    LaunchedEffect(uiState.isPurchased) {
        if (uiState.isPurchased) {
            delay(1200)
            onBackClick()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.White,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .offset(x = (-12).dp) // Neutralizes the 12dp internal padding so it aligns flush left
                        .size(48.dp)
                        .testTag("paywall_back_button")
                ) {
                    Icon(
                        imageVector = KithIcons.ArrowBack,
                        contentDescription = "Back",
                        tint = TextDark,
                        modifier = Modifier.size(24.dp)
                    )
                }

                AnnualMonthlyPillToggle(
                    selectedCycle = uiState.selectedCycle,
                    onSelect = { cycle ->
                        viewModel.onEvent(PaywallUiEvent.SelectCycle(cycle))
                    }
                )
            }

            Text(
                text = "Pricing",
                fontFamily = OutfitFontFamily,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp) // Prevents it from stretching too wide
                    .defaultMinSize(minHeight = 500.dp) // Forces the container to be longer vertically
                    .clip(RoundedCornerShape(32.dp))
                    .background(PaywallBlue)
                    .padding(horizontal = 24.dp, vertical = 36.dp) // Increased vertical padding
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = KithIcons.Crown,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "KITH PREMIUM",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    AnimatedContent(
                        targetState = Pair(uiState.activePrimaryPrice, uiState.activeSubPrice),
                        transitionSpec = {
                            (slideInVertically(
                                initialOffsetY = { -it / 2 },
                                animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
                            ) + fadeIn(animationSpec = tween(durationMillis = 280))) togetherWith
                                    (slideOutVertically(
                                        targetOffsetY = { it / 2 },
                                        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
                                    ) + fadeOut(animationSpec = tween(durationMillis = 280)))
                        },
                        label = "PricingTextTransition"
                    ) { (primaryPrice, subPrice) ->
                        Column {
                            Text(
                                text = primaryPrice,
                                color = Color.White,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 32.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = subPrice,
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Experience the full potential of Kith with unlimited requests, community unlocks, and priority attention from campus experts.",
                        color = Color.White.copy(alpha = 0.90f),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    uiState.features.forEach { feature ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp) // Added breathing room between list items
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.22f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = KithIcons.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = feature,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = {
                            if (activity != null) {
                                viewModel.onEvent(PaywallUiEvent.Purchase(activity))
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("paywall_upgrade_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = PaywallBlue
                        ),
                        enabled = !uiState.isPurchasing && !uiState.isLoading
                    ) {
                        if (uiState.isPurchasing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = PaywallBlue,
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Text(
                                text = if (uiState.isPurchased) "Already Premium" else "Upgrade to Premium",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(
                    onClick = { viewModel.onEvent(PaywallUiEvent.Restore) },
                    enabled = !uiState.isRestoring && !uiState.isPurchasing,
                    modifier = Modifier.testTag("paywall_restore_button")
                ) {
                    if (uiState.isRestoring) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = "Restore Purchases",
                        color = TextMuted,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AnnualMonthlyPillToggle(
    selectedCycle: BillingCycle,
    onSelect: (BillingCycle) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isAnnual = selectedCycle == BillingCycle.ANNUAL

    val annualBgColor by animateColorAsState(
        targetValue = if (isAnnual) PaywallBlue else Color.Transparent,
        animationSpec = tween(durationMillis = 220),
        label = "AnnualBgColor"
    )
    val annualTextColor by animateColorAsState(
        targetValue = if (isAnnual) Color.White else TextMuted,
        animationSpec = tween(durationMillis = 220),
        label = "AnnualTextColor"
    )
    val monthlyBgColor by animateColorAsState(
        targetValue = if (!isAnnual) PaywallBlue else Color.Transparent,
        animationSpec = tween(durationMillis = 220),
        label = "MonthlyBgColor"
    )
    val monthlyTextColor by animateColorAsState(
        targetValue = if (!isAnnual) Color.White else TextMuted,
        animationSpec = tween(durationMillis = 220),
        label = "MonthlyTextColor"
    )

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(LightPillBackground)
            .border(width = 1.dp, color = PillBorderColor, shape = RoundedCornerShape(50))
            .padding(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(annualBgColor)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { onSelect(BillingCycle.ANNUAL) }
                )
                .padding(horizontal = 14.dp, vertical = 7.dp)
                .testTag("paywall_toggle_annual"),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Annual",
                color = annualTextColor,
                fontSize = 13.sp,
                fontWeight = if (isAnnual) FontWeight.Bold else FontWeight.Medium
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(monthlyBgColor)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { onSelect(BillingCycle.MONTHLY) }
                )
                .padding(horizontal = 14.dp, vertical = 7.dp)
                .testTag("paywall_toggle_monthly"),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Monthly",
                color = monthlyTextColor,
                fontSize = 13.sp,
                fontWeight = if (!isAnnual) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}