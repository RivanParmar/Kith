package com.kith.feature.community.impl

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale

/**
 * A single community entry shown in the list.
 *
 * [imageRes] is left nullable for now -- swap in a painterResource(...) or a
 * Coil AsyncImage once real community photos are wired up. Until then a
 * placeholder avatar box is drawn instead.
 *
 * [memberCount] is the ONLY piece of data expected to change for now
 * (added to / subtracted from) -- title, image, etc are static per the
 * current requirement. This is plain data, not yet backed by editable UI.
 */
data class Community(
    val id: String,
    val title: String,
    val memberCount: Int,
    val imageRes: Int? = null
)

// Sample data matching the reference screenshot -- pass your own list into
// CommunitiesScreen(communities = ...) to override this.
private val sampleCommunities = listOf(
    Community(id = "1", title = "Stuck on React Hook state update bug", memberCount = 1240),
    Community(id = "2", title = "Stuck on React Hook state update bug", memberCount = 1240),
    Community(id = "3", title = "Stuck on React Hook state update bug", memberCount = 1240)
)

/**
 * "Communities" list screen.
 *
 * - [communities]: the list to render. Defaults to sample data matching the
 *   reference design.
 * - [onCommunityClick]: fired when a community card is tapped. Left empty
 *   by default -- wire up navigation/logic from the caller.
 *
 * The bottom nav bar is intentionally static/visual only for now (no click
 * behavior), with "Browse" shown as the highlighted tab to match the
 * reference screenshot.
 */
@Composable
fun CommunitiesScreen(
    communities: List<Community> = sampleCommunities,
    onCommunityClick: (Community) -> Unit = {}
) {
    Scaffold(
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
        ) {
            Text(
                text = "Communities",
                color = Color(0xFF1B1F3B),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)
            )

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(communities, key = { it.id }) { community ->
                    CommunityCard(
                        community = community,
                        onClick = { onCommunityClick(community) }
                    )
                }
                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
private fun CommunityCard(
    community: Community,
    onClick: () -> Unit
) {
    val memberCountFormatted = NumberFormat.getNumberInstance(Locale.US).format(community.memberCount)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFECEDF3)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar placeholder -- swap for painterResource(community.imageRes)
            // or an AsyncImage once real photos are available.
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFE3E6F0)),
                contentAlignment = Alignment.Center
            ) {

            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = community.title,
                    color = Color(0xFF1B1F3B),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 19.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$memberCountFormatted Members",
                    color = Color(0xFFA0A3B1),
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 780)
@Composable
fun CommunitiesScreenPreview() {
    CommunitiesScreen()
}