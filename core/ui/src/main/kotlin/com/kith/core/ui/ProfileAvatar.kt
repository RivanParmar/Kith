package com.kith.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.kith.core.designsystem.icon.KithIcons
import com.kith.core.designsystem.theme.KithTheme
import com.kith.core.model.data.User
import com.kith.core.model.data.UserProfile

private val PremiumRingGradient = Brush.sweepGradient(
    listOf(
        Color(0xFFFFDF00), // Bright Gold
        Color(0xFFF59E0B), // Amber Gold
        Color(0xFFFFD700), // Pure Gold
        Color(0xFFFBBF24), // Radiant Yellow Gold
        Color(0xFFF59E0B), // Warm Amber
        Color(0xFFFFDF00), // Return to Bright Gold
    )
)

/**
 * A container that adds a golden ring around any circular avatar content
 * when [isPremium] is true.
 */
@Composable
fun RingProfileAvatar(
    isPremium: Boolean,
    modifier: Modifier = Modifier,
    avatarSize: Dp = 40.dp,
    onClick: (() -> Unit)? = null,
    avatarContent: @Composable () -> Unit,
) {
    val clickableModifier = if (onClick != null) {
        Modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
    } else {
        Modifier
    }

    if (!isPremium) {
        Box(
            modifier = modifier
                .size(avatarSize)
                .then(clickableModifier),
            contentAlignment = Alignment.Center,
        ) {
            avatarContent()
        }
    } else {
        val ringThickness = (avatarSize * 0.055f).coerceIn(2.dp, 3.5.dp)
        val ringGap = (avatarSize * 0.04f).coerceIn(1.5.dp, 3.dp)

        Box(
            modifier = modifier
                .size(avatarSize)
                .then(clickableModifier)
                .border(
                    width = ringThickness,
                    brush = PremiumRingGradient,
                    shape = CircleShape,
                )
                .padding(ringThickness + ringGap),
            contentAlignment = Alignment.Center,
        ) {
            avatarContent()
        }
    }
}


/**
 * Renders a user's profile avatar. If [isPremium] is true, a golden ring is displayed
 * encircling the avatar.
 */
@Composable
fun ProfileAvatar(
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    name: String = "",
    isPremium: Boolean = false,
    avatarSize: Dp = 40.dp,
    onClick: (() -> Unit)? = null,
) {
    RingProfileAvatar(
        isPremium = isPremium,
        modifier = modifier,
        avatarSize = avatarSize,
        onClick = onClick,
    ) {
        AvatarContent(
            imageUrl = imageUrl,
            name = name,
            avatarSize = avatarSize,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape),
        )
    }
}

@Composable
fun ProfileAvatar(
    user: User,
    modifier: Modifier = Modifier,
    avatarSize: Dp = 40.dp,
    onClick: (() -> Unit)? = null,
) = ProfileAvatar(
    imageUrl = user.profileImageUrl,
    name = user.name,
    isPremium = user.isPremium,
    modifier = modifier,
    avatarSize = avatarSize,
    onClick = onClick,
)

@Composable
fun ProfileAvatar(
    userProfile: UserProfile?,
    modifier: Modifier = Modifier,
    avatarSize: Dp = 40.dp,
    onClick: (() -> Unit)? = null,
) = ProfileAvatar(
    imageUrl = userProfile?.profileImageUrl,
    name = userProfile?.name ?: "",
    isPremium = userProfile?.isPremium ?: false,
    modifier = modifier,
    avatarSize = avatarSize,
    onClick = onClick,
)

@Composable
private fun AvatarContent(
    imageUrl: String?,
    name: String,
    avatarSize: Dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        if (!imageUrl.isNullOrEmpty()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else if (name.isNotBlank()) {
            val initials = name.trim().split("\\s+".toRegex())
                .mapNotNull { it.firstOrNull()?.toString() }
                .take(2)
                .joinToString("")
                .uppercase()

            val fontSize = (avatarSize.value * 0.38f).coerceAtLeast(11f).sp

            Text(
                text = initials.ifEmpty { "U" },
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            Icon(
                imageVector = KithIcons.Person,
                contentDescription = "User profile picture",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size((avatarSize * 0.55f).coerceAtLeast(16.dp)),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileAvatarPreview() {
    KithTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ProfileAvatar(
                name = "Emily Watson",
                isPremium = false,
                avatarSize = 40.dp,
            )
            ProfileAvatar(
                name = "Brandon Miller",
                isPremium = true,
                avatarSize = 40.dp,
            )
            ProfileAvatar(
                name = "VIP King",
                isPremium = true,
                avatarSize = 64.dp,
            )
        }
    }
}
