package com.example.bountyapp.ui.postdetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// =============================================================================
// 1. DATA MODELS (Dynamic Page State)
// =============================================================================

data class Author(
    val name: String,
    val department: String,
    val role: String,
    val rating: Double,
    val reviewCount: Int,
    val avatarUrl: String? = null
)

data class BountyReward(
    val guaranteedLabel: String = "Guaranteed Reward",
    val title: String = "Completed Bounty",
    val xpAmount: Int = 350
)

data class PostDetail(
    val id: String,
    val title: String,
    val author: Author,
    val tags: List<String>,
    val description: String,
    val bounty: BountyReward,
    val isAccepted: Boolean = false
)

// =============================================================================
// 2. COLOR PALETTE & DESIGN TOKENS
// =============================================================================

object PostDetailColors {
    val Background = Color(0xFFFFFFFF)
    val TextPrimary = Color(0xFF111827)
    val TextSecondary = Color(0xFF4B5563)
    val TextMuted = Color(0xFF9CA3AF)

    // Author card
    val AuthorCardBackground = Color(0xFFF3F4F6)
    val RatingStar = Color(0xFFF59E0B)

    // Dynamic Tags
    val TagBackground = Color(0xFFEFF4FF)
    val TagText = Color(0xFF6366F1)

    // Bounty Card
    val BountyCardBackground = Color(0xFFF0F4FF)
    val BountyCardBorder = Color(0xFFA5B4FC)
    val BountyTitle = Color(0xFF4F46E5)
    val BountyXp = Color(0xFF3B82F6)

    // Accept Request Button Gradient (Exact match to reference image: light periwinkle to deep cobalt)
    val GradientStart = Color(0xFF98B5F2) // Soft periwinkle / pastel powder blue (left)
    val GradientMid = Color(0xFF5588EF)   // Vibrant transition blue (center)
    val GradientEnd = Color(0xFF1E5FF5)   // Deep cobalt / electric royal blue (right)

    // Accepted State Transition Colors
    val AcceptedGradientStart = Color(0xFF059669)
    val AcceptedGradientEnd = Color(0xFF10B981)
}

// =============================================================================
// 3. MAIN COMPOSABLE: Dynamic Post Detail Screen (Single Screen)
// =============================================================================

/**
 * Dynamic Post Detail Screen in Kotlin (Jetpack Compose)
 * - Single-screen layout matching reference image.
 * - Pinned Accept Request button in bottom bar with smooth color animation.
 * - No bottom navbar and no separate after-accept page.
 *
 * @param post Dynamic data object containing all fields displayed on screen.
 * @param onBackClick Action when the back navigation arrow is tapped.
 * @param onAcceptClick Action when user clicks "Accept Request".
 * @param onTagClick Callback when any tag is tapped.
 * @param onAuthorClick Callback when the author card is tapped.
 */
@Composable
fun PostDetailScreen(
    post: PostDetail,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onAcceptClick: () -> Unit = {},
    onTagClick: (String) -> Unit = {},
    onAuthorClick: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = PostDetailColors.Background,
        topBar = {
            PostDetailTopBar(onBackClick = onBackClick)
        },
        bottomBar = {
            // Pinned at the bottom of the page (no navbar)
            AcceptRequestBottomBar(
                isAccepted = post.isAccepted,
                onClick = onAcceptClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Author Profile Card
            AuthorProfileCard(
                author = post.author,
                onClick = onAuthorClick
            )

            // Dynamic Post Title
            Text(
                text = post.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PostDetailColors.TextPrimary,
                lineHeight = 32.sp
            )

            // Dynamic Topic Badges / Tags
            FlowTagsRow(
                tags = post.tags,
                onTagClick = onTagClick
            )

            // Body Description
            Text(
                text = post.description,
                fontSize = 15.sp,
                color = PostDetailColors.TextSecondary,
                lineHeight = 22.sp
            )

            // Bounty / XP Guaranteed Reward Card
            BountyRewardCard(bounty = post.bounty)

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// =============================================================================
// 4. SUB-COMPONENTS
// =============================================================================

@Composable
fun PostDetailTopBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = PostDetailColors.TextPrimary
            )
        }
    }
}

@Composable
fun AuthorProfileCard(
    author: Author,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = PostDetailColors.AuthorCardBackground
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circular Avatar Placeholder / Image
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFCBD5E1)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = author.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155),
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = author.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PostDetailColors.TextPrimary
                )
                Text(
                    text = "${author.department} • ${author.role}",
                    fontSize = 16.sp,
                    color = PostDetailColors.TextSecondary
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Rating",
                        tint = PostDetailColors.RatingStar,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${author.rating} (${author.reviewCount} reviews)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = PostDetailColors.TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun FlowTagsRow(
    tags: List<String>,
    onTagClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tags.forEach { tag ->
            Surface(
                shape = RoundedCornerShape(50),
                color = PostDetailColors.TagBackground,
                modifier = Modifier.clickable { onTagClick(tag) }
            ) {
                Text(
                    text = tag,
                    color = PostDetailColors.TagText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun BountyRewardCard(
    bounty: BountyReward,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(
            containerColor = PostDetailColors.BountyCardBackground
        ),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.5.dp, PostDetailColors.BountyCardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = bounty.guaranteedLabel,
                    fontSize = 13.sp,
                    color = PostDetailColors.TextSecondary,
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = bounty.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PostDetailColors.BountyTitle
                )
            }

            Text(
                text = "${bounty.xpAmount} XP",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PostDetailColors.BountyXp
            )
        }
    }
}

/**
 * Pinned Bottom Bar containing the Accept Request button with exact gradient from image.
 */
@Composable
fun AcceptRequestBottomBar(
    isAccepted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = PostDetailColors.Background,
        shadowElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            AcceptRequestButton(
                isAccepted = isAccepted,
                onClick = onClick
            )
        }
    }
}

/**
 * Accept Request button with smooth animated color gradient transition.
 * When not accepted: Uses the exact periwinkle-to-cobalt gradient from the reference image.
 * When accepted: Transitions to green with checkmark icon.
 */
@Composable
fun AcceptRequestButton(
    isAccepted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedStartColor by animateColorAsState(
        targetValue = if (isAccepted) PostDetailColors.AcceptedGradientStart else PostDetailColors.GradientStart,
        animationSpec = tween(durationMillis = 400),
        label = "StartColorAnimation"
    )
    val animatedEndColor by animateColorAsState(
        targetValue = if (isAccepted) PostDetailColors.AcceptedGradientEnd else PostDetailColors.GradientEnd,
        animationSpec = tween(durationMillis = 400),
        label = "EndColorAnimation"
    )
    val animatedShadowColor by animateColorAsState(
        targetValue = if (isAccepted) Color(0x5010B981) else Color(0x351E5FF5),
        animationSpec = tween(durationMillis = 400),
        label = "ShadowColorAnimation"
    )

    val buttonBrush = if (isAccepted) {
        Brush.horizontalGradient(listOf(animatedStartColor, animatedEndColor))
    } else {
        Brush.horizontalGradient(
            listOf(
                PostDetailColors.GradientStart,
                PostDetailColors.GradientMid,
                PostDetailColors.GradientEnd
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(28.dp),
                spotColor = animatedShadowColor
            )
            .clip(RoundedCornerShape(28.dp))
            .background(buttonBrush)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(visible = isAccepted) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
            Text(
                text = if (isAccepted) "Request Accepted" else "Accept Request",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

// =============================================================================
// 5. DYNAMIC STATE ROUTE (Single-Screen Interactive Dynamic Page)
// =============================================================================

@Composable
fun DynamicPostDetailRoute(
    initialPost: PostDetail = SamplePostDetailData
) {
    var postState by remember { mutableStateOf(initialPost) }

    PostDetailScreen(
        post = postState,
        onBackClick = { /* Handle back navigation */ },
        onAcceptClick = {
            // Toggles accepted state with smooth color animation on the same page
            postState = postState.copy(isAccepted = !postState.isAccepted)
        },
        onTagClick = { /* Filter or navigate by tag */ },
        onAuthorClick = { /* Navigate to author profile */ }
    )
}

// =============================================================================
// 6. SAMPLE DATA & PREVIEWS
// =============================================================================

val SamplePostDetailData = PostDetail(
    id = "post-econ-310",
    title = "Help editing Econ 310 Research Paper draft",
    author = Author(
        name = "Elena Rostova",
        department = "Economics Department",
        role = "Senior",
        rating = 4.9,
        reviewCount = 42,
        avatarUrl = null
    ),
    tags = listOf("Virtual", "Community-Name"),
    description = "Need a second pair of eyes to proofread and correct reference style citations on my Econ research proposal draft. It is about 5 pages. I will be sitting at the library desk until 4 PM!",
    bounty = BountyReward(
        guaranteedLabel = "Guaranteed Reward",
        title = "Completed Bounty",
        xpAmount = 350
    ),
    isAccepted = false
)

@Preview(name = "1. Post Detail Default", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun PreviewPostDetailScreenDefault() {
    MaterialTheme {
        DynamicPostDetailRoute()
    }
}

@Preview(name = "2. Post Detail Accepted", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun PreviewPostDetailScreenAccepted() {
    MaterialTheme {
        PostDetailScreen(
            post = SamplePostDetailData.copy(isAccepted = true)
        )
    }
}
