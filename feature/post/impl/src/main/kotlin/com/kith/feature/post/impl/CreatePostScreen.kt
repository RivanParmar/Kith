package com.kith.feature.post.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import com.kith.core.designsystem.icon.KithIcons
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kith.core.designsystem.theme.PrimaryBlue
import com.kith.core.model.data.Community
import com.kith.core.model.data.Post
import com.kith.core.model.data.User
import kotlin.time.Clock

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreatePostScreen(
    onBackClick: () -> Unit = {},
    onPostCreated: (Post) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var reward by remember { mutableIntStateOf(100) }
    var isInPerson by remember { mutableStateOf(false) }
    var selectedCommunityIndex by remember { mutableIntStateOf(0) }
    var titleError by remember { mutableStateOf(false) }
    var contentError by remember { mutableStateOf(false) }

    val communities = remember {
        listOf(
            Community("c1", "Software Engineering", null),
            Community("c2", "Economics Department", null),
            Community("c3", "Mathematics & Physics", null),
            Community("c4", "Design & Arts", null),
            Community("c5", "Campus Life", null)
        )
    }

    val rewardOptions = listOf(50, 100, 200, 350, 500)

    val buttonBrush = Brush.horizontalGradient(
        listOf(
            PostDetailColors.GradientStart,
            PostDetailColors.GradientMid,
            PostDetailColors.GradientEnd
        )
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("create_post_back_button")
                ) {
                    Icon(
                        imageVector = KithIcons.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Create Request",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(28.dp),
                                spotColor = Color(0x351E5FF5)
                            )
                            .clip(RoundedCornerShape(28.dp))
                            .background(buttonBrush)
                            .clickable {
                                titleError = title.isBlank()
                                contentError = content.isBlank()
                                if (!titleError && !contentError) {
                                    val newPost = Post(
                                        id = "post-${System.currentTimeMillis()}",
                                        title = title.trim(),
                                        content = content.trim(),
                                        reward = reward,
                                        author = User(
                                            id = "current_user",
                                            name = "You",
                                            profileImageUrl = null,
                                            isPremium = true,
                                            rating = 5.0f
                                        ),
                                        community = communities[selectedCommunityIndex],
                                        createdAt = Clock.System.now(),
                                        isInPerson = isInPerson
                                    )
                                    onPostCreated(newPost)
                                }
                            }
                            .testTag("submit_create_post_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = KithIcons.Add,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Post Request",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Title Input
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Request Title",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (titleError && it.isNotBlank()) titleError = false
                    },
                    placeholder = { Text("What do you need help with?") },
                    isError = titleError,
                    supportingText = if (titleError) {
                        { Text("Title is required") }
                    } else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("post_title_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    singleLine = true
                )
            }

            // Community Selection
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Select Community",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    communities.forEachIndexed { index, community ->
                        FilterChip(
                            selected = selectedCommunityIndex == index,
                            onClick = { selectedCommunityIndex = index },
                            label = { Text(community.name, fontSize = 13.sp) },
                            shape = RoundedCornerShape(20.dp),
                            leadingIcon = if (selectedCommunityIndex == index) {
                                {
                                    Icon(
                                        imageVector = KithIcons.Check,
                                        contentDescription = "Selected",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFEFF6FF),
                                selectedLabelColor = PrimaryBlue,
                                selectedLeadingIconColor = PrimaryBlue
                            )
                        )
                    }
                }
            }

            // Description Input
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Description",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = {
                        content = it
                        if (contentError && it.isNotBlank()) contentError = false
                    },
                    placeholder = { Text("Describe what you need help with in detail...") },
                    isError = contentError,
                    supportingText = if (contentError) {
                        { Text("Description is required") }
                    } else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .testTag("post_content_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    maxLines = 6
                )
            }

            // Bounty / Reward
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF0F4FF)
                ),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFA5B4FC))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Bounty Reward",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4F46E5)
                            )
                            Text(
                                text = "Offered upon completion",
                                fontSize = 12.sp,
                                color = Color(0xFF6B7280)
                            )
                        }
                        Text(
                            text = "$reward XP",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryBlue
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rewardOptions.forEach { amount ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (reward == amount) PrimaryBlue else Color.White,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { reward = amount }
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "+$amount",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (reward == amount) Color.White else Color(0xFF374151)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Format / Location Toggle
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Request Format",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilterChip(
                        selected = !isInPerson,
                        onClick = { isInPerson = false },
                        label = { Text("Remote / Online") },
                        leadingIcon = {
                            Icon(
                                imageVector = KithIcons.Groups,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFEFF6FF),
                            selectedLabelColor = PrimaryBlue,
                            selectedLeadingIconColor = PrimaryBlue
                        )
                    )
                    FilterChip(
                        selected = isInPerson,
                        onClick = { isInPerson = true },
                        label = { Text("In-Person") },
                        leadingIcon = {
                            Icon(
                                imageVector = KithIcons.Person,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFEFF6FF),
                            selectedLabelColor = PrimaryBlue,
                            selectedLeadingIconColor = PrimaryBlue
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreatePostScreenPreview() {
    MaterialTheme {
        CreatePostScreen(onBackClick = {})
    }
}
