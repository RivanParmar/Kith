package com.kith.feature.post.impl

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ==========================================
// 1. DATA MODEL
// ==========================================
data class PostData(
    val title: String = "",
    val description: String = "",
    val difficulty: String = "",
    val bounty: String = "",
    val community: String = "Community 1",
    val isInPerson: Boolean = false,
    val postType: String = "Post",
    val attachments: List<String> = emptyList()
)

// ==========================================
// 2. COLOR PALETTE
// ==========================================
private val DarkBackground = Color(0xFF14161A)
private val ScreenBackground = Color(0xFFFFFFFF)
private val TextPrimary = Color(0xFF111827)
private val TextSecondary = Color(0xFF475569)
private val TextMuted = Color(0xFF8E98A8)
private val BorderMuted = Color(0xFF8E98A8)
private val CancelBlue = Color(0xFF557B9E)
private val PrimaryBlue = Color(0xFF2563EB)
private val DarkNavyBlue = Color(0xFF30446F)
private val BountyCircleColor = Color(0xFF5D6B80)

// ==========================================
// 3. MAIN DYNAMIC SCREEN COMPOSABLE
// ==========================================
@Composable
fun CreatePostScreen(
    onCancelClick: () -> Unit = {},
    onPreviewClick: (PostData) -> Unit = {},
    onPostClick: (PostData) -> Unit = {},
    onDraftClick: (PostData) -> Unit = {},
    onTabSelected: (String) -> Unit = {}
) {
    // Dynamic Form States
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedDifficulty by remember { mutableStateOf("") }
    var selectedBounty by remember { mutableStateOf("") }
    var selectedCommunity by remember { mutableStateOf("Community 1") }
    var isCommunityDropdownExpanded by remember { mutableStateOf(false) }
    var isInPerson by remember { mutableStateOf(false) }
    var postType by remember { mutableStateOf("Post") } // "Post" or "Draft"
    val attachments = remember { mutableStateListOf<String>() }

    val communities = listOf("Community 1", "Community 2", "Tech Innovators", "Design Guild")

    // Current dynamic PostData snapshot
    val currentPostData = PostData(
        title = title,
        description = description,
        difficulty = selectedDifficulty,
        bounty = selectedBounty,
        community = selectedCommunity,
        isInPerson = isInPerson,
        postType = postType,
        attachments = attachments.toList()
    )

    // Dark outer frame
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        // Mobile device surface
        Surface(
            modifier = Modifier
                .width(392.dp)
                .fillMaxHeight(),
            shape = RoundedCornerShape(42.dp),
            color = ScreenBackground,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // 1. Status Bar
                StatusBar()

                // 2. Header Bar
                HeaderBar(
                    onCancel = onCancelClick,
                    onPreview = { onPreviewClick(currentPostData) }
                )

                // 3. Scrollable Dynamic Content Area
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 4.dp)
                ) {
                    // Title Field with Floating Label
                    FloatingTitleInput(
                        value = title,
                        onValueChange = { title = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Media Attachment Row
                    AttachmentButtonsRow(
                        onAddPhoto = {
                            attachments.add("Photo_${attachments.size + 1}.jpg")
                        },
                        onAddDocument = {
                            attachments.add("Doc_${attachments.size + 1}.pdf")
                        },
                        onAttachLink = {
                            attachments.add("https://link_${attachments.size + 1}.ref")
                        }
                    )

                    // Dynamic Attachment Chips
                    if (attachments.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        DynamicAttachmentChips(
                            attachments = attachments,
                            onRemove = { index -> attachments.removeAt(index) }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Description Area with matching Floating Label on border line
                    DescriptionInput(
                        value = description,
                        onValueChange = { description = it }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Dynamic Select Difficulty Level & Choose Bounty Row
                    DifficultyBountyRow(
                        selectedDifficulty = selectedDifficulty,
                        onDifficultySelected = { selectedDifficulty = it },
                        selectedBounty = selectedBounty,
                        onBountySelected = { selectedBounty = it }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Dynamic Community Dropdown & In-Person Checkbox
                    CommunityAndInPersonRow(
                        selectedCommunity = selectedCommunity,
                        expanded = isCommunityDropdownExpanded,
                        onExpandedChange = { isCommunityDropdownExpanded = it },
                        communities = communities,
                        onSelectCommunity = { selectedCommunity = it },
                        isInPerson = isInPerson,
                        onToggleInPerson = { isInPerson = it }
                    )

                    // Flexible vertical spacing pushing button to comfortable distance
                    Spacer(modifier = Modifier.weight(1f, fill = true))
                    Spacer(modifier = Modifier.height(32.dp))

                    // Unified Split Button (Gradient Pill with Divider & Down Chevron)
                    PostButtonGroup(
                        postType = postType,
                        onPostTypeChange = { postType = it },
                        onAction = {
                            if (postType == "Post") {
                                onPostClick(currentPostData)
                            } else {
                                onDraftClick(currentPostData)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

// ==========================================
// 4. SUB-COMPONENTS
// ==========================================

@Composable
fun StatusBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 28.dp, end = 28.dp, top = 14.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "9:41",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.height(11.dp)
            ) {
                Box(Modifier.size(width = 3.dp, height = 3.dp).background(TextPrimary, RoundedCornerShape(0.5.dp)))
                Box(Modifier.size(width = 3.dp, height = 5.dp).background(TextPrimary, RoundedCornerShape(0.5.dp)))
                Box(Modifier.size(width = 3.dp, height = 8.dp).background(TextPrimary, RoundedCornerShape(0.5.dp)))
                Box(Modifier.size(width = 3.dp, height = 11.dp).background(TextPrimary, RoundedCornerShape(0.5.dp)))
            }

            Text(
                text = "4G",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(start = 2.dp)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(width = 22.dp, height = 12.dp)
                        .border(1.dp, TextPrimary, RoundedCornerShape(3.dp))
                        .padding(1.5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.8f)
                            .background(TextPrimary, RoundedCornerShape(1.dp))
                    )
                }
                Box(
                    modifier = Modifier
                        .size(width = 1.5.dp, height = 4.dp)
                        .background(TextPrimary, RoundedCornerShape(topEnd = 1.dp, bottomEnd = 1.dp))
                )
            }
        }
    }
}

@Composable
fun HeaderBar(
    onCancel: () -> Unit,
    onPreview: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Cancel",
            color = CancelBlue,
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.clickable { onCancel() }
        )

        Text(
            text = "Create Post",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        OutlinedButton(
            onClick = onPreview,
            shape = CircleShape,
            border = BorderStroke(1.dp, PrimaryBlue),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 2.dp),
            modifier = Modifier.height(28.dp)
        ) {
            Text("Preview", fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

// ----------------------------------------------------------------------------
// Title Field with Floating Label
// ----------------------------------------------------------------------------
@Composable
fun FloatingTitleInput(
    value: String,
    onValueChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .border(1.dp, BorderMuted, RoundedCornerShape(6.dp))
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (value.isEmpty()) {
                Text(
                    text = "Enter Post Title Here",
                    color = TextPrimary,
                    fontSize = 14.sp
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(fontSize = 14.sp, color = TextPrimary),
                cursorBrush = SolidColor(PrimaryBlue),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Box(
            modifier = Modifier
                .offset(x = 10.dp, y = (-7).dp)
                .background(ScreenBackground)
                .padding(horizontal = 4.dp)
        ) {
            Text(
                text = "Title",
                fontSize = 11.sp,
                color = TextSecondary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun AttachmentButtonsRow(
    onAddPhoto: () -> Unit,
    onAddDocument: () -> Unit,
    onAttachLink: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {

    }
}

@Composable
fun DynamicAttachmentChips(
    attachments: List<String>,
    onRemove: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        attachments.forEachIndexed { index, name ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F5F9))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = name,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(4.dp))

            }
        }
    }
}

@Composable
fun AttachmentItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            )
            .padding(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = TextSecondary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextSecondary
        )
    }
}

// ----------------------------------------------------------------------------
// Description Field with matching Floating Label on border line
// ----------------------------------------------------------------------------
@Composable
fun DescriptionInput(
    value: String,
    onValueChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(136.dp)
                .border(1.dp, BorderMuted, RoundedCornerShape(6.dp))
                .padding(horizontal = 12.dp, vertical = 12.dp),
            contentAlignment = Alignment.TopStart
        ) {
            if (value.isEmpty()) {
                Text(
                    text = "Enter Post Description Here",
                    color = TextPrimary,
                    fontSize = 13.5.sp,
                    lineHeight = 19.sp
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(
                    fontSize = 13.5.sp,
                    color = TextPrimary,
                    lineHeight = 19.sp
                ),
                cursorBrush = SolidColor(PrimaryBlue),
                modifier = Modifier.fillMaxSize()
            )
        }

        Box(
            modifier = Modifier
                .offset(x = 10.dp, y = (-7).dp)
                .background(ScreenBackground)
                .padding(horizontal = 4.dp)
        ) {
            Text(
                text = "Description",
                fontSize = 11.sp,
                color = TextSecondary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ----------------------------------------------------------------------------
// Dynamic Difficulty + Bounty Row with Interactive Selection Menus
// ----------------------------------------------------------------------------
@Composable
fun DifficultyBountyRow(
    selectedDifficulty: String,
    onDifficultySelected: (String) -> Unit,
    selectedBounty: String,
    onBountySelected: (String) -> Unit
) {
    var difficultyMenuExpanded by remember { mutableStateOf(false) }
    var bountyMenuExpanded by remember { mutableStateOf(false) }
    val difficultyOptions = listOf("Easy", "Medium", "Hard")
    val bountyOptions = listOf("$10", "$25", "$50", "$100", "Custom")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .border(1.dp, BorderMuted, RoundedCornerShape(9.dp))
            .padding(horizontal = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Dynamic Difficulty selector
        Box {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { difficultyMenuExpanded = true }
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = if (selectedDifficulty.isNotEmpty()) selectedDifficulty else "Select Difficulty Level",
                    fontSize = 13.5.sp,
                    fontWeight = if (selectedDifficulty.isNotEmpty()) FontWeight.SemiBold else FontWeight.Medium,
                    color = if (selectedDifficulty.isNotEmpty()) PrimaryBlue else Color(0xFF334155)
                )
                Spacer(modifier = Modifier.width(4.dp))

            }

            DropdownMenu(
                expanded = difficultyMenuExpanded,
                onDismissRequest = { difficultyMenuExpanded = false }
            ) {
                difficultyOptions.forEach { level ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = level,
                                fontSize = 13.sp,
                                fontWeight = if (selectedDifficulty == level) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedDifficulty == level) PrimaryBlue else TextPrimary
                            )
                        },
                        onClick = {
                            onDifficultySelected(level)
                            difficultyMenuExpanded = false
                        }
                    )
                }
            }
        }

        // Center separator divider
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(20.dp)
                .background(BorderMuted.copy(alpha = 0.4f))
        )

        // Right: Dynamic Choose Bounty selector
        Box {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { bountyMenuExpanded = true }
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = if (selectedBounty.isNotEmpty()) selectedBounty else "Choose Bounty",
                    fontSize = 13.5.sp,
                    fontWeight = if (selectedBounty.isNotEmpty()) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (selectedBounty.isNotEmpty()) PrimaryBlue else TextMuted
                )
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .background(if (selectedBounty.isNotEmpty()) PrimaryBlue else BountyCircleColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            DropdownMenu(
                expanded = bountyMenuExpanded,
                onDismissRequest = { bountyMenuExpanded = false }
            ) {
                bountyOptions.forEach { bounty ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = bounty,
                                fontSize = 13.sp,
                                fontWeight = if (selectedBounty == bounty) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedBounty == bounty) PrimaryBlue else TextPrimary
                            )
                        },
                        onClick = {
                            onBountySelected(bounty)
                            bountyMenuExpanded = false
                        }
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------
// Dynamic Community Dropdown & In-Person Checkbox
// ----------------------------------------------------------------------------
@Composable
fun CommunityAndInPersonRow(
    selectedCommunity: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    communities: List<String>,
    onSelectCommunity: (String) -> Unit,
    isInPerson: Boolean,
    onToggleInPerson: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.weight(0.58f)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .border(1.dp, BorderMuted, RoundedCornerShape(9.dp))
                    .clickable { onExpandedChange(!expanded) }
                    .padding(horizontal = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedCommunity,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF334155)
                )

            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) }
            ) {
                communities.forEach { community ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = community,
                                fontSize = 13.sp,
                                fontWeight = if (selectedCommunity == community) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedCommunity == community) PrimaryBlue else TextPrimary
                            )
                        },
                        onClick = {
                            onSelectCommunity(community)
                            onExpandedChange(false)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { onToggleInPerson(!isInPerson) }
                .padding(vertical = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(19.dp)
                    .border(
                        width = 1.5.dp,
                        color = if (isInPerson) PrimaryBlue else BorderMuted,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .background(
                        color = if (isInPerson) PrimaryBlue else Color.White,
                        shape = RoundedCornerShape(4.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isInPerson) {

                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "In-Person",
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary
            )
        }
    }
}

// ----------------------------------------------------------------------------
// Unified Split Post / Draft Button (Gradient pill with integrated divider & arrow)
// ----------------------------------------------------------------------------
@Composable
fun PostButtonGroup(
    postType: String,
    onPostTypeChange: (String) -> Unit,
    onAction: () -> Unit
) {
    var optionsMenuExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Single continuous gradient pill container matching the design
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(25.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(DarkNavyBlue, Color(0xFF2851A3), PrimaryBlue)
                    )
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Main Action clickable area: "Post" or "Draft"
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onAction() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = postType,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.3.sp
                )
            }

            // Subtle vertical dividing separator
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(28.dp)
                    .background(Color.White.copy(alpha = 0.28f))
            )

            // Companion Dropdown trigger area with down arrow
            Box(
                modifier = Modifier
                    .width(52.dp)
                    .fillMaxHeight()
                    .clickable { optionsMenuExpanded = true },
                contentAlignment = Alignment.Center
            ) {

            }
        }

        // Dropdown Menu for selecting "Post" or "Draft"
        DropdownMenu(
            expanded = optionsMenuExpanded,
            onDismissRequest = { optionsMenuExpanded = false }
        ) {
            listOf("Post", "Draft").forEach { type ->
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = type,
                                fontSize = 14.sp,
                                fontWeight = if (postType == type) FontWeight.Bold else FontWeight.Normal,
                                color = if (postType == type) PrimaryBlue else TextPrimary
                            )
                            if (postType == type) {

                            }
                        }
                    },
                    onClick = {
                        onPostTypeChange(type)
                        optionsMenuExpanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCreatePostScreen() {
    CreatePostScreen()
}
