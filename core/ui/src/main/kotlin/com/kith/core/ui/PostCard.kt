package com.kith.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kith.core.designsystem.icon.bolt
import com.kith.core.designsystem.icon.star_rate
import com.kith.core.model.data.Community
import com.kith.core.model.data.Post
import com.kith.core.model.data.User
import kotlin.time.Clock
@Composable
fun PostCard(
    post: Post,
    modifier: Modifier = Modifier
) {
    val lightBlueBg = Color(0xFFF0F5FF)
    val brightBlueText = Color(0xFF0056D6)
    val starColor = Color(0xFFFDB814)
    val timeStampColor = Color(0xFFA0AEC0)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(Color.White)
//        color = Color.White,
        // Adding a subtle elevation shadow like in the design
//        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // --- Top Row (Profile Info) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. User Profile Picture
                // Assuming a painter resource ID for now; replace with Coil's AsyncImage for URLs
                // Using a placeholder image
                Image(
                    painter = painterResource(id = android.R.drawable.ic_menu_camera), // Replace with your user icon
                    contentDescription = "User profile picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant) // Placeholder background
                )

                Spacer(modifier = Modifier.width(12.dp))

                // 2. Name and Rating Column
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = post.author.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = star_rate,
                            contentDescription = null,
                            tint = starColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${post.author.rating}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                    }
                }

                // 3. XP Reward Tag
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(lightBlueBg)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Using a bolt-like icon, replace with the correct asset
                    Icon(
                        imageVector = bolt, // Replace with lightning bolt asset
                        contentDescription = null,
                        tint = brightBlueText,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${post.reward} XP",
                        color = brightBlueText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = post.title,
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth(),
                lineHeight = 25.sp
            )

//            Spacer(modifier = Modifier.height(7.dp))

            // --- Description ---
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth(),
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- Bottom Row (Timestamp) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            )
            {
                Text(
                    text = "In-person",
                    color = brightBlueText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .background(lightBlueBg, shape = RoundedCornerShape(6.dp))
                        .padding(6.dp)
                )

                Text(
                    text = post.createdAt.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = timeStampColor
                )
            }
        }
    }
}

@Preview
@Composable
private fun PostCardPreview() {
    PostCard(
        post = Post(
            "1", "Title","Content",5, User("1", "Preet patel", null, false,4.5f),
            Community("1", "Abc", null), Clock.System.now(), true
        )
    )
}