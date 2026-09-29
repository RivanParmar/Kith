package com.kith.feature.post.impl

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kith.core.designsystem.icon.KithIcons
import com.kith.core.model.data.User
import java.util.UUID

// =============================================================================
// ATTACHMENT MODEL
// =============================================================================

data class AttachedFile(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val size: String,
    val isDocument: Boolean = false,
    val uri: String? = null
)

// =============================================================================
// COLOR PALETTE & DESIGN TOKENS
// =============================================================================

internal object PostDetailColors {
    val Background = Color(0xFFFFFFFF)
    val TextPrimary = Color(0xFF111827)
    val TextSecondary = Color(0xFF4B5563)
    val TextMuted = Color(0xFF6B7280)
    val AuthorCardBackground = Color(0xFFF3F4F6)
    val RatingStar = Color(0xFFF59E0B)
    val TagBackground = Color(0xFFEFF4FF)
    val TagText = Color(0xFF6366F1)
    val BountyCardBackground = Color(0xFFF0F4FF)
    val BountyCardBorder = Color(0xFFA5B4FC)
    val BountyTitle = Color(0xFF4F46E5)
    val BountyXp = Color(0xFF3B82F6)
    val PrimaryBlue = Color(0xFF2563EB)
    val GradientStart = Color(0xFF98B5F2)
    val GradientMid = Color(0xFF5588EF)
    val GradientEnd = Color(0xFF1E5FF5)
    val AcceptedGradientStart = Color(0xFF059669)
    val AcceptedGradientEnd = Color(0xFF10B981)
    val BorderLight = Color(0xFFE5E7EB)
    val MediaPlaceholderBackground = Color(0xFFEBEDF2)
    val SolutionCardBackground = Color(0xFFF7F6FB)
}

// =============================================================================
// ICONS (Self-contained vector definitions)
// =============================================================================

private val DeleteIcon: ImageVector
    get() = ImageVector.Builder(
        name = "Delete",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color(0xFFEF4444)),
            pathFillType = PathFillType.NonZero
        ) {
            moveTo(6f, 19f)
            curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
            horizontalLineToRelative(8f)
            curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
            verticalLineTo(7f)
            horizontalLineTo(6f)
            verticalLineToRelative(12f)
            close()
            moveTo(19f, 4f)
            horizontalLineToRelative(-3.5f)
            lineToRelative(-1f, -1f)
            horizontalLineToRelative(-5f)
            lineToRelative(-1f, 1f)
            horizontalLineTo(5f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(14f)
            verticalLineTo(4f)
            close()
        }
    }.build()

private val ArrowDropDownIcon: ImageVector
    get() = ImageVector.Builder(
        name = "ArrowDropDown",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF111827)),
            pathFillType = PathFillType.NonZero
        ) {
            moveTo(7f, 10f)
            lineToRelative(5f, 5f)
            lineToRelative(5f, -5f)
            close()
        }
    }.build()

private val ArrowDropUpIcon: ImageVector
    get() = ImageVector.Builder(
        name = "ArrowDropUp",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF111827)),
            pathFillType = PathFillType.NonZero
        ) {
            moveTo(7f, 14f)
            lineToRelative(5f, -5f)
            lineToRelative(5f, 5f)
            close()
        }
    }.build()

private val PaperclipIcon: ImageVector
    get() = ImageVector.Builder(
        name = "AttachFile",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF4B5563)),
            pathFillType = PathFillType.NonZero
        ) {
            moveTo(16.5f, 6f)
            verticalLineToRelative(11.5f)
            curveToRelative(0f, 2.21f, -1.79f, 4f, -4f, 4f)
            reflectiveCurveToRelative(-4f, -1.79f, -4f, -4f)
            verticalLineTo(5f)
            curveToRelative(0f, -1.38f, 1.12f, -2.5f, 2.5f, -2.5f)
            reflectiveCurveToRelative(2.5f, 1.12f, 2.5f, 2.5f)
            verticalLineToRelative(10.5f)
            curveToRelative(0f, 0.55f, -0.45f, 1f, -1f, 1f)
            reflectiveCurveToRelative(-1f, -0.45f, -1f, -1f)
            verticalLineTo(6f)
            horizontalLineTo(10f)
            verticalLineToRelative(9.5f)
            curveToRelative(0f, 1.38f, 1.12f, 2.5f, 2.5f, 2.5f)
            reflectiveCurveToRelative(2.5f, -1.12f, 2.5f, -2.5f)
            verticalLineTo(5f)
            curveToRelative(0f, -2.21f, -1.79f, -4f, -4f, -4f)
            reflectiveCurveToRelative(-4f, 1.79f, -4f, 4f)
            verticalLineToRelative(12.5f)
            curveToRelative(0f, 3.04f, 2.46f, 5.5f, 5.5f, 5.5f)
            reflectiveCurveToRelative(5.5f, -2.46f, 5.5f, -5.5f)
            verticalLineTo(6f)
            horizontalLineToRelative(-1.5f)
            close()
        }
    }.build()

private val CloseIcon: ImageVector
    get() = ImageVector.Builder(
        name = "Close",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF9CA3AF)),
            pathFillType = PathFillType.NonZero
        ) {
            moveTo(19f, 6.41f)
            lineTo(17.59f, 5f)
            lineTo(12f, 10.59f)
            lineTo(6.41f, 5f)
            lineTo(5f, 6.41f)
            lineTo(10.59f, 12f)
            lineTo(5f, 17.59f)
            lineTo(6.41f, 19f)
            lineTo(12f, 13.41f)
            lineTo(17.59f, 19f)
            lineTo(19f, 17.59f)
            lineTo(13.41f, 12f)
            close()
        }
    }.build()

private val DocumentIcon: ImageVector
    get() = ImageVector.Builder(
        name = "Description",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF2563EB)),
            pathFillType = PathFillType.NonZero
        ) {
            moveTo(14f, 2f)
            horizontalLineTo(6f)
            curveToRelative(-1.1f, 0f, -1.99f, 0.9f, -1.99f, 2f)
            lineTo(4f, 20f)
            curveToRelative(0f, 1.1f, 0.89f, 2f, 1.99f, 2f)
            horizontalLineTo(18f)
            curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
            verticalLineTo(8f)
            lineToRelative(-6f, -6f)
            close()
            moveTo(13f, 9f)
            verticalLineTo(3.5f)
            lineTo(18.5f, 9f)
            horizontalLineTo(13f)
            close()
            moveTo(16f, 17f)
            horizontalLineTo(8f)
            verticalLineToRelative(-2f)
            horizontalLineToRelative(8f)
            verticalLineToRelative(2f)
            close()
            moveTo(16f, 13f)
            horizontalLineTo(8f)
            verticalLineToRelative(-2f)
            horizontalLineToRelative(8f)
            verticalLineToRelative(2f)
            close()
        }
    }.build()

private val ImageIcon: ImageVector
    get() = ImageVector.Builder(
        name = "Image",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF64748B)),
            pathFillType = PathFillType.NonZero
        ) {
            moveTo(21f, 19f)
            verticalLineTo(5f)
            curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
            horizontalLineTo(5f)
            curveToRelative(-1.1f, 0f, -2f, 0.9f, -2f, 2f)
            verticalLineToRelative(14f)
            curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
            horizontalLineToRelative(14f)
            curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
            close()
            moveTo(8.5f, 13.5f)
            lineToRelative(2.5f, 3.01f)
            lineToRelative(3.5f, -4.51f)
            lineToRelative(4.5f, 6f)
            horizontalLineTo(5f)
            lineToRelative(3.5f, -4.5f)
            close()
        }
    }.build()

// =============================================================================
// MAIN ENTRY POINT COMPOSABLE
// =============================================================================

@Composable
fun PostDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: PostDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onCommunityClick: (String) -> Unit = {},
    onAuthorClick: (String) -> Unit = {},
    onAttachFileClick: (() -> Unit)? = null,
    onRateSolution: (Int) -> Unit = {},
    onSubmitSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PostDetailContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onDeleteClick = { viewModel.onDeletePost(onBackClick) },
        onAcceptClick = viewModel::onAcceptClick,
        onSubmitAnswer = { answer, _ ->
            viewModel.onSubmitAnswer(answer)
            onSubmitSuccess()
        },
        onCommunityClick = onCommunityClick,
        onAuthorClick = onAuthorClick,
        onAttachFileClick = onAttachFileClick,
        onRateSolution = { rating ->
            viewModel.onRateSolution(rating)
            onRateSolution(rating)
        },
        onAcceptSolution = viewModel::onAcceptSolution,
        onRejectSolution = viewModel::onRejectSolution,
        modifier = modifier
    )
}

@Composable
private fun PostDetailContent(
    uiState: PostDetailUiState,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onAcceptClick: () -> Unit = {},
    onSubmitAnswer: (String, List<AttachedFile>) -> Unit = { _, _ -> },
    onCommunityClick: (String) -> Unit = {},
    onAuthorClick: (String) -> Unit = {},
    onAttachFileClick: (() -> Unit)? = null,
    onRateSolution: (Int) -> Unit = {},
    onAcceptSolution: () -> Unit = {},
    onRejectSolution: () -> Unit = {}
) {
    when (uiState) {
        is PostDetailUiState.Loading -> {
            Scaffold(
                modifier = modifier.fillMaxSize(),
                containerColor = PostDetailColors.Background,
                topBar = { PostDetailTopBar(onBackClick = onBackClick) }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PostDetailColors.BountyTitle)
                }
            }
        }
        is PostDetailUiState.Error -> {
            Scaffold(
                modifier = modifier.fillMaxSize(),
                containerColor = PostDetailColors.Background,
                topBar = { PostDetailTopBar(onBackClick = onBackClick) }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.message ?: "Failed to load post details",
                        color = PostDetailColors.TextSecondary,
                        fontSize = 15.sp
                    )
                }
            }
        }
        is PostDetailUiState.Success -> {
            val post = uiState.post
            val isAnswerReceived = !post.answer.isNullOrBlank()

            var isAttachmentsExpanded by remember(post.id) { mutableStateOf(false) }

            var answerText by remember(post.id) { mutableStateOf("") }
            val attachedFiles = remember(post.id, uiState.isAcceptedByCurrentUser) {
                mutableStateListOf<AttachedFile>().apply {
                    if (uiState.isAcceptedByCurrentUser) {
                        add(AttachedFile(name = "research_notes.pdf", size = "2.1 MB", isDocument = true))
                    }
                }
            }

            var userRating by remember(post.id) { mutableIntStateOf(uiState.userRating) }

            val handleAttachFile: () -> Unit = {
                if (onAttachFileClick != null) {
                    onAttachFileClick()
                } else {
                    val count = attachedFiles.size + 1
                    attachedFiles.add(
                        AttachedFile(
                            name = "attachment_$count.pdf",
                            size = "1.5 MB",
                            isDocument = true
                        )
                    )
                }
            }

            Scaffold(
                modifier = modifier.fillMaxSize(),
                containerColor = PostDetailColors.Background,
                topBar = {
                    PostDetailTopBar(
                        onBackClick = onBackClick,
                        showDeleteButton = uiState.isAuthor,
                        onDeleteClick = onDeleteClick
                    )
                },
                bottomBar = {
                    if (!uiState.isAuthor && !isAnswerReceived) {
                        if (!uiState.isAcceptedByCurrentUser) {
                            AcceptRequestBottomBar(
                                isAccepted = false,
                                onClick = onAcceptClick
                            )
                        } else {
                            SubmitAnswerBottomBar(
                                onSubmit = {
                                    onSubmitAnswer(answerText, attachedFiles.toList())
                                }
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .imePadding()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    // 1. Author Profile Card
                    AuthorProfileCard(
                        author = post.author,
                        onClick = { onAuthorClick(post.author.id) }
                    )

                    // 2. Title Row with Dropdown Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = post.title,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = PostDetailColors.TextPrimary,
                            lineHeight = 28.sp,
                            modifier = Modifier.weight(1f)
                        )
                        if (isAnswerReceived) {
                            IconButton(
                                onClick = { isAttachmentsExpanded = !isAttachmentsExpanded },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (isAttachmentsExpanded) ArrowDropUpIcon else ArrowDropDownIcon,
                                    contentDescription = if (isAttachmentsExpanded) "Collapse attachments" else "Expand attachments",
                                    tint = PostDetailColors.TextPrimary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }

                    // 3. Post Tags Row
                    PostTagsRow(
                        isInPerson = post.isInPerson,
                        communityName = post.community.name,
                        onCommunityClick = { onCommunityClick(post.community.id) }
                    )

                    // 4. Question / Content Description


                    // 5. Attachments Section
                    if (isAnswerReceived) {
                        AnimatedVisibility(
                            visible = isAttachmentsExpanded,
                            enter = expandVertically(animationSpec = tween(300)) + fadeIn(),
                            exit = shrinkVertically(animationSpec = tween(300)) + fadeOut()
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {

                                Text(
                                    text = post.content,
                                    fontSize = 14.5.sp,
                                    color = PostDetailColors.TextSecondary,
                                    lineHeight = 21.sp
                                )

                                PostMediaAttachments()

                                if (attachedFiles.isNotEmpty()) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        attachedFiles.forEach { file ->
                                            AttachedFileChip(
                                                file = file,
                                                onRemove = null
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else if (!uiState.isAcceptedByCurrentUser) {
                        PostMediaAttachments()
                    }

                    // 6. Answer / Solution Section
                    val currentAnswer = post.answer
                    if (isAnswerReceived && !currentAnswer.isNullOrBlank()) {
                        SolutionCard(solution = currentAnswer)

                        if (uiState.isAuthor) {
                            RateSolutionRow(
                                rating = userRating,
                                onRatingChange = { newRating ->
                                    userRating = newRating
                                    onRateSolution(newRating)
                                }
                            )

                            // Accept or Reject Solution UI for Author
                            SolutionDecisionRow(
                                status = uiState.solutionStatus,
                                onAccept = onAcceptSolution,
                                onReject = onRejectSolution
                            )
                        }

                        BountyRewardCard(reward = post.reward, isCompleted = true)
                    } else if (!uiState.isAuthor && uiState.isAcceptedByCurrentUser) {
                        BountyRewardCard(reward = post.reward, isCompleted = false)

                        YourAnswerSection(
                            answerText = answerText,
                            onAnswerChange = { answerText = it },
                            attachedFiles = attachedFiles,
                            onAttachFileClick = handleAttachFile,
                            onRemoveFile = { file -> attachedFiles.remove(file) }
                        )
                    } else {
                        BountyRewardCard(reward = post.reward, isCompleted = false)
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

// =============================================================================
// SUB-COMPONENTS
// =============================================================================

@Composable
private fun PostDetailTopBar(
    onBackClick: () -> Unit,
    showDeleteButton: Boolean = false,
    onDeleteClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick, modifier = Modifier.size(40.dp)) {
            Icon(
                imageVector = KithIcons.ArrowBack,
                contentDescription = "Back",
                tint = PostDetailColors.TextPrimary
            )
        }

        if (showDeleteButton) {
            IconButton(onClick = onDeleteClick, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = DeleteIcon,
                    contentDescription = "Delete Post",
                    tint = Color(0xFFEF4444)
                )
            }
        }
    }
}

@Composable
private fun SolutionDecisionRow(
    status: SolutionStatus,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (status) {
        SolutionStatus.PENDING -> {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onAccept,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Accept Solution", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFEF4444))
                ) {
                    Text("Reject Solution", color = Color(0xFFEF4444), fontWeight = FontWeight.SemiBold)
                }
            }
        }
        SolutionStatus.ACCEPTED -> {
            Surface(
                modifier = modifier.fillMaxWidth(),
                color = Color(0xFFD1FAE5),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "✓ Solution Accepted",
                    color = Color(0xFF065F46),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(14.dp)
                )
            }
        }
        SolutionStatus.REJECTED -> {
            Surface(
                modifier = modifier.fillMaxWidth(),
                color = Color(0xFFFEE2E2),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "✕ Solution Rejected",
                    color = Color(0xFF991B1B),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(14.dp)
                )
            }
        }
    }
}

@Composable
private fun AuthorProfileCard(author: User, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = PostDetailColors.AuthorCardBackground),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFCBD5E1)),
                contentAlignment = Alignment.Center
            ) {
                if (!author.profileImageUrl.isNullOrEmpty()) {
                    Image(
                        painter = painterResource(android.R.drawable.ic_menu_camera),
                        contentDescription = author.name,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        text = author.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155),
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                Text(
                    text = author.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PostDetailColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Economics Department • Senior",
                    fontSize = 12.sp,
                    color = PostDetailColors.TextSecondary
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = KithIcons.StarRate,
                        contentDescription = "Rating",
                        tint = PostDetailColors.RatingStar,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${author.rating} (42 reviews)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = PostDetailColors.TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun PostTagsRow(
    isInPerson: Boolean,
    communityName: String,
    onCommunityClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(shape = RoundedCornerShape(50), color = PostDetailColors.TagBackground) {
            Text(
                text = if (isInPerson) "In-Person" else "Virtual",
                color = PostDetailColors.TagText,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            )
        }
        if (communityName.isNotBlank()) {
            Surface(
                shape = RoundedCornerShape(50),
                color = PostDetailColors.TagBackground,
                modifier = Modifier.clickable(onClick = onCommunityClick)
            ) {
                Text(
                    text = communityName,
                    color = PostDetailColors.TagText,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun PostMediaAttachments(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .width(220.dp)
                .height(155.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(PostDetailColors.MediaPlaceholderBackground),
            contentAlignment = Alignment.Center
        ) {
            PlaceholderMediaGraphic(modifier = Modifier.size(90.dp))
        }

        Box(
            modifier = Modifier
                .width(64.dp)
                .height(155.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(PostDetailColors.MediaPlaceholderBackground),
            contentAlignment = Alignment.CenterStart
        ) {
            Box(modifier = Modifier.padding(start = 10.dp)) {
                PlaceholderMediaGraphic(modifier = Modifier.size(45.dp))
            }
        }
    }
}

@Composable
private fun PlaceholderMediaGraphic(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val shapeColor = Color(0xFFC7CBD3)

        drawCircle(
            color = shapeColor,
            radius = w * 0.19f,
            center = Offset(w * 0.50f, h * 0.32f)
        )

        drawCircle(
            color = shapeColor,
            radius = w * 0.17f,
            center = Offset(w * 0.30f, h * 0.68f)
        )

        drawRoundRect(
            color = shapeColor,
            topLeft = Offset(w * 0.55f, h * 0.52f),
            size = Size(w * 0.32f, w * 0.32f),
            cornerRadius = CornerRadius(14f, 14f)
        )
    }
}

@Composable
private fun SolutionCard(
    solution: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PostDetailColors.SolutionCardBackground),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Solution",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = PostDetailColors.TextPrimary
            )
            Text(
                text = solution,
                fontSize = 14.sp,
                color = PostDetailColors.TextSecondary,
                lineHeight = 21.sp
            )
        }
    }
    PlaceholderMediaGraphic()
}

@Composable
private fun RateSolutionRow(
    rating: Int,
    onRatingChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Rate the Solution : ",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            fontStyle = FontStyle.Italic,
            fontFamily = FontFamily.Serif,
            color = PostDetailColors.TextPrimary
        )
        Spacer(modifier = Modifier.width(6.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 1..5) {
                Icon(
                    imageVector = KithIcons.StarRate,
                    contentDescription = "Star $i",
                    tint = if (i <= rating) PostDetailColors.RatingStar else Color(0xFFD1D5DB),
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onRatingChange(i) }
                )
            }
        }
    }
}

@Composable
private fun BountyRewardCard(
    reward: Int,
    isCompleted: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PostDetailColors.BountyCardBackground),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.2.dp, PostDetailColors.BountyCardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (isCompleted) "Completed Bounty" else "Guaranteed Reward",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isCompleted) PostDetailColors.BountyXp else PostDetailColors.TextPrimary
            )
            Text(
                text = "$reward XP",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PostDetailColors.BountyXp
            )
        }
    }
}

@Composable
private fun YourAnswerSection(
    answerText: String,
    onAnswerChange: (String) -> Unit,
    attachedFiles: List<AttachedFile>,
    onAttachFileClick: () -> Unit,
    onRemoveFile: (AttachedFile) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text = "Your Answer",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = PostDetailColors.TextPrimary
            )
            Text(
                text = "Provide your solution to complete this task",
                fontSize = 13.5.sp,
                color = PostDetailColors.TextMuted
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 135.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(BorderStroke(1.dp, PostDetailColors.BorderLight), RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp)
        ) {
            if (answerText.isEmpty()) {
                Text(
                    text = "Write your solution or answer...",
                    color = Color(0xFF9CA3AF),
                    fontSize = 14.5.sp
                )
            }
            BasicTextField(
                value = answerText,
                onValueChange = onAnswerChange,
                textStyle = TextStyle(
                    fontSize = 14.5.sp,
                    color = PostDetailColors.TextPrimary,
                    lineHeight = 22.sp
                ),
                cursorBrush = SolidColor(PostDetailColors.PrimaryBlue),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onAttachFileClick)
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = PaperclipIcon,
                contentDescription = "Attach files",
                tint = Color(0xFF4B5563),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Attach files",
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PostDetailColors.TextPrimary
                )
                Text(
                    text = "Images, documents, or other files",
                    fontSize = 12.sp,
                    color = PostDetailColors.TextMuted
                )
            }
        }

        if (attachedFiles.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                attachedFiles.forEach { file ->
                    AttachedFileChip(
                        file = file,
                        onRemove = { onRemoveFile(file) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AttachedFileChip(
    file: AttachedFile,
    onRemove: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, PostDetailColors.BorderLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(
                start = 8.dp,
                end = if (onRemove != null) 10.dp else 14.dp,
                top = 8.dp,
                bottom = 8.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (file.isDocument) Color(0xFFEFF6FF) else Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (file.isDocument) DocumentIcon else ImageIcon,
                    contentDescription = null,
                    tint = if (file.isDocument) Color(0xFF2563EB) else Color(0xFF64748B),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = file.name,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PostDetailColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = file.size,
                    fontSize = 11.5.sp,
                    color = PostDetailColors.TextMuted
                )
            }

            if (onRemove != null) {
                Spacer(modifier = Modifier.width(10.dp))
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = CloseIcon,
                        contentDescription = "Remove file",
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}

// =============================================================================
// BOTTOM BARS
// =============================================================================

@Composable
private fun AcceptRequestBottomBar(
    isAccepted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        AcceptRequestButton(isAccepted = isAccepted, onClick = onClick)
    }
}

@Composable
private fun AcceptRequestButton(
    isAccepted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedStartColor by animateColorAsState(
        targetValue = if (isAccepted) PostDetailColors.AcceptedGradientStart else PostDetailColors.GradientStart,
        animationSpec = tween(400)
    )
    val animatedEndColor by animateColorAsState(
        targetValue = if (isAccepted) PostDetailColors.AcceptedGradientEnd else PostDetailColors.GradientEnd,
        animationSpec = tween(400)
    )
    val animatedShadowColor by animateColorAsState(
        targetValue = if (isAccepted) Color(0x5010B981) else Color(0x351E5FF5),
        animationSpec = tween(400)
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
            .height(54.dp)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(28.dp), spotColor = animatedShadowColor)
            .clip(RoundedCornerShape(28.dp))
            .background(buttonBrush)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isAccepted) "Request Accepted" else "Accept Request",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }
}

@Composable
private fun SubmitAnswerBottomBar(
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(14.dp),
                    spotColor = Color(0x351E5FF5)
                )
                .clip(RoundedCornerShape(14.dp))
                .background(PostDetailColors.PrimaryBlue)
                .clickable(onClick = onSubmit),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Submit Answer",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

    // =============================================================================
// PREVIEWS
// =============================================================================

    @Preview(name = "1. Loading", showBackground = true, widthDp = 390, heightDp = 844)
    @Composable
    private fun PreviewLoading() {
        MaterialTheme {
            PostDetailContent(uiState = PostDetailUiState.Loading)
        }
    }

    @Preview(name = "2. Error", showBackground = true, widthDp = 390, heightDp = 844)
    @Composable
    private fun PreviewError() {
        MaterialTheme {
            PostDetailContent(uiState = PostDetailUiState.Error("Something went wrong"))
        }
    }

    @Preview(name = "3. Visitor - Not Accepted", showBackground = true, widthDp = 390, heightDp = 844)
    @Composable
    private fun PreviewVisitorNotAccepted() {
        MaterialTheme {
            PostDetailContent(
                uiState = PostDetailUiState.Success(
                    post = samplePostDetail.copy(answer = null),
                    isAuthor = false,
                    isAcceptedByCurrentUser = false
                )
            )
        }
    }

    @Preview(name = "4. Solver - Accepted (Answer Form)", showBackground = true, widthDp = 390, heightDp = 844)
    @Composable
    private fun PreviewSolverAccepted() {
        MaterialTheme {
            PostDetailContent(
                uiState = PostDetailUiState.Success(
                    post = samplePostDetail.copy(answer = null),
                    isAuthor = false,
                    isAcceptedByCurrentUser = true
                )
            )
        }
    }

    @Preview(name = "5. Author - Waiting For Answer", showBackground = true, widthDp = 390, heightDp = 844)
    @Composable
    private fun PreviewAuthorWaiting() {
        MaterialTheme {
            PostDetailContent(
                uiState = PostDetailUiState.Success(
                    post = samplePostDetail.copy(answer = null),
                    isAuthor = true,
                    isAcceptedByCurrentUser = false
                )
            )
        }
    }

    @Preview(name = "6. Author - Answer Pending Decision", showBackground = true, widthDp = 390, heightDp = 844)
    @Composable
    private fun PreviewAuthorAnswerPending() {
        MaterialTheme {
            PostDetailContent(
                uiState = PostDetailUiState.Success(
                    post = samplePostDetailAnswered,
                    isAuthor = true,
                    isAcceptedByCurrentUser = false,
                    userRating = 4,
                    solutionStatus = SolutionStatus.PENDING
                )
            )
        }
    }

    @Preview(name = "7. Author - Solution Accepted", showBackground = true, widthDp = 390, heightDp = 844)
    @Composable
    private fun PreviewAuthorSolutionAccepted() {
        MaterialTheme {
            PostDetailContent(
                uiState = PostDetailUiState.Success(
                    post = samplePostDetailAnswered,
                    isAuthor = true,
                    isAcceptedByCurrentUser = false,
                    userRating = 5,
                    solutionStatus = SolutionStatus.ACCEPTED
                )
            )
        }
    }

    @Preview(name = "8. Author - Solution Rejected", showBackground = true, widthDp = 390, heightDp = 844)
    @Composable
    private fun PreviewAuthorSolutionRejected() {
        MaterialTheme {
            PostDetailContent(
                uiState = PostDetailUiState.Success(
                    post = samplePostDetailAnswered,
                    isAuthor = true,
                    isAcceptedByCurrentUser = false,
                    userRating = 2,
                    solutionStatus = SolutionStatus.REJECTED
                )
            )
        }
    }

    @Preview(name = "9. Solver - Answer Sent (Read Only)", showBackground = true, widthDp = 390, heightDp = 844)
    @Composable
    private fun PreviewSolverAnswerSent() {
        MaterialTheme {
            PostDetailContent(
                uiState = PostDetailUiState.Success(
                    post = samplePostDetailAnswered,
                    isAuthor = false,
                    isAcceptedByCurrentUser = true
                )
            )
        }
    }