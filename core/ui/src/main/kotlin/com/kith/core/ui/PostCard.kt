package com.kith.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kith.core.designsystem.icon.KithIcons
import com.kith.core.designsystem.theme.KithTheme
import com.kith.core.designsystem.theme.OutfitFontFamily
import com.kith.core.model.data.Post
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import kotlin.time.Instant
import kotlin.time.toJavaInstant

@Composable
fun PostCard(
    post: Post,
    modifier: Modifier = Modifier,
    onPostClick: (String) -> Unit = { _ -> },
    onAuthorClick: (String) -> Unit = { _ -> },
) {
    val extendedColors = KithTheme.extendedColors

    OutlinedCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        onClick = { onPostClick(post.id) },
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onAuthorClick(post.author.id) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProfileAvatar(user = post.author)

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = post.author.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = KithIcons.StarRate,
                                contentDescription = null,
                                tint = extendedColors.postCardStar, // Themed Star
                                modifier = Modifier.size(14.dp),
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${post.author.rating}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = extendedColors.postCardRatingText, // Themed Rating Text
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(extendedColors.postCardBadgeBg) // Themed Badge Bg
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = KithIcons.Bolt,
                        contentDescription = null,
                        tint = extendedColors.postCardBadgeText, // Themed Badge Text/Icon
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${post.reward} XP",
                        color = extendedColors.postCardBadgeText, // Themed Badge Text
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = post.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth(),
                lineHeight = 25.sp,
                maxLines = 1,
            )

            Text(
                text = post.content,
                style = MaterialTheme.typography.bodySmall,
                color = extendedColors.postCardContentText, // Themed Content Text
                modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                lineHeight = 22.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (post.isInPerson) "In-person" else "Virtual",
                    color = extendedColors.postCardBadgeText, // Themed Badge Text
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .background(extendedColors.postCardBadgeBg, shape = RoundedCornerShape(6.dp)) // Themed Badge Bg
                        .padding(6.dp),
                )

                val formattedDate = remember(post.createdAt) { dateFormatted(post.createdAt) }
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = extendedColors.postCardDateText, // Themed Date Text
                )
            }
        }
    }
}

private fun dateFormatted(publishDate: Instant): String = DateTimeFormatter
    .ofLocalizedDate(FormatStyle.MEDIUM)
    .withLocale(Locale.getDefault())
    .withZone(ZoneId.systemDefault())
    .format(publishDate.toJavaInstant())

@Preview
@Composable
private fun PostCardPreview(
    @PreviewParameter(PostPreviewParameterProvider::class)
    posts: List<Post>,
) {
    KithTheme {
        PostCard(
            post = posts[0],
            onPostClick = {},
            onAuthorClick = {}
        )
    }
}