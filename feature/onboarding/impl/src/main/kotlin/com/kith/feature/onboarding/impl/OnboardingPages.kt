package com.kith.feature.onboarding.impl

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.kith.core.designsystem.icon.KithIcons
import com.kith.feature.onboarding.api.R
import kotlin.math.abs

@Composable
fun OnboardingPageOne(
    page: Int,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 72.dp)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(bottom = 140.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "KITH",
                color = Color.White,
                fontSize = 56.sp,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(top = 52.dp),
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(top = 12.dp)
                .graphicsLayer {
                    val pageOffset =
                        (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                    alpha = (1f - abs(pageOffset) * 2f).coerceIn(0f, 1f)
                },
        ) {
            Text(
                text = stringResource(R.string.feature_onboarding_api_heading_page_one),
                color = Color.White,
                fontSize = 40.sp,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                lineHeight = 42.sp,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.feature_onboarding_api_content_page_one),
                fontSize = 24.sp,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun OnboardingPageTwo(
    page: Int,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .zIndex(2f)
            .padding(horizontal = 72.dp)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(bottom = 140.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(0.8f))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(0f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = Color(0xFF111115)
                ),
                elevation = CardDefaults.cardElevation(18.dp),
            ) {
                Column(
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painter = painterResource(android.R.drawable.ic_menu_camera),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Sample",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                            )
                        }
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFF0F5FF))
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = KithIcons.Bolt,
                                contentDescription = null,
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "50 XP",
                                color = Color(0xFF2563EB),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Title",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "In-person",
                            color = Color(0xFF2563EB),
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 10.sp,
                            modifier = Modifier
                                .background(
                                    Color(0xFFF0F5FF), RoundedCornerShape(6.dp)
                                )
                                .padding(vertical = 2.dp, horizontal = 4.dp)
                        )

                        Text(
                            text = "5m ago",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFF0F5FF),
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.weight(0.6f))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(top = 12.dp)
                .graphicsLayer {
                    val pageOffset =
                        (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                    alpha = (1f - abs(pageOffset) * 2f).coerceIn(0f, 1f)
                },
        ) {
            Text(
                text = stringResource(R.string.feature_onboarding_api_heading_page_two),
                color = Color.White,
                fontSize = 40.sp,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                lineHeight = 42.sp,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.feature_onboarding_api_content_page_two),
                fontSize = 24.sp,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OnboardingPageThree(
    page: Int,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .zIndex(2f)
            .padding(start = 62.dp, top = 82.dp, end = 62.dp)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(bottom = 140.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(3.5f))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(top = 12.dp)
                .graphicsLayer {
                    val pageOffset =
                        (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                    alpha = (1f - abs(pageOffset) * 2f).coerceIn(0f, 1f)
                },
        ) {
            Text(
                text = "Earn XP & Build\nTrust",
                color = Color.White,
                fontSize = 36.sp,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                lineHeight = 42.sp,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Receive XP bounties guaranteed upon completing favors. Level up your reputation tier, unlock exclusive campus perks, and gain verified trust.",
                fontSize = 24.sp,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun OnboardingPageFour(
    page: Int,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(bottom = 140.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .wrapContentWidth()
                .padding(top = 72.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CommunityListItem(
                    R.string.feature_onboarding_api_community_item_title_one,
                    R.string.feature_onboarding_api_community_item_supporting_one,
                    modifier = Modifier.align(Alignment.End),
                )
                CommunityListItem(
                    R.string.feature_onboarding_api_community_item_title_two,
                    R.string.feature_onboarding_api_community_item_supporting_two,
                    modifier = Modifier.align(Alignment.Start),
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(top = 12.dp)
                .padding(horizontal = 72.dp)
                .graphicsLayer {
                    val pageOffset =
                        (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                    alpha = (1f - abs(pageOffset) * 2f).coerceIn(0f, 1f)
                },
        ) {
            Text(
                text = stringResource(R.string.feature_onboarding_api_heading_page_four),
                color = Color.White,
                fontSize = 40.sp,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                lineHeight = 42.sp,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.feature_onboarding_api_content_page_four),
                fontSize = 24.sp,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun CommunityListItem(
    @StringRes titleTextRes: Int,
    @StringRes supportingTextRes: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth(0.75f)
            .background(Color(0xFF111115), RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = painterResource(android.R.drawable.ic_menu_camera),
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
        )
        Column {
            Text(
                text = stringResource(titleTextRes),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
            Text(
                text = stringResource(supportingTextRes),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
            )
        }
    }
}