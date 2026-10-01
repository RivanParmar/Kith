package com.kith.feature.post.impl

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.kith.core.designsystem.icon.KithIcons
import com.kith.core.designsystem.theme.KithExtendedColors
import com.kith.core.designsystem.theme.KithTheme
import com.kith.core.model.data.User
import java.util.UUID

data class AttachedFile(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val size: String,
    val isDocument: Boolean = false,
    val uri: String? = null
)

private object PostDetailColors {
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
    val SolutionCardBackground = Color(0xFFF7F6FB)
}

@Composable
fun PostDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: PostDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onCommunityClick: (String) -> Unit = {},
    onAuthorClick: (String) -> Unit = {},
    onNavigateToMyProfile: () -> Unit = {},
    onRateSolution: (Float) -> Unit = {},
    onSubmitSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PostDetailScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onDeleteClick = { viewModel.onDeletePost(onBackClick) },
        onAcceptClick = viewModel::onAcceptClick,
        onSubmitAnswer = { answer, files ->
            viewModel.onSubmitAnswer(answer, files)
            onSubmitSuccess()
        },
        onCommunityClick = onCommunityClick,
        onAuthorClick = onAuthorClick,
        onNavigateToMyProfile = onNavigateToMyProfile,
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
internal fun PostDetailScreen(
    uiState: PostDetailUiState,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onAcceptClick: () -> Unit = {},
    onSubmitAnswer: (String, List<AttachedFile>) -> Unit = { _, _ -> },
    onCommunityClick: (String) -> Unit = {},
    onAuthorClick: (String) -> Unit = {},
    onNavigateToMyProfile: () -> Unit = {},
    onRateSolution: (Float) -> Unit = {},
    onAcceptSolution: () -> Unit = {},
    onRejectSolution: () -> Unit = {},
) {
    val mediaPicker = LocalMediaPickerHelper.current
    val extendedColors = KithTheme.extendedColors

    when (uiState) {
        is PostDetailUiState.Loading -> {
            Scaffold(
                modifier = modifier.fillMaxSize(),
                containerColor = extendedColors.detailBg,
                topBar = {
                    PostDetailTopBar(
                        extendedColors = extendedColors,
                        onBackClick = onBackClick,
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularWavyProgressIndicator(color = extendedColors.detailBountyTitle)
                }
            }
        }
        is PostDetailUiState.Error -> {
            Scaffold(
                modifier = modifier.fillMaxSize(),
                containerColor = extendedColors.detailBg,
                topBar = {
                    PostDetailTopBar(
                        extendedColors = extendedColors,
                        onBackClick = onBackClick,
                    )
                }
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

            var isAttachmentsExpanded by remember(post.id) { mutableStateOf(!isAnswerReceived) }
            var answerText by remember(post.id) { mutableStateOf("") }

            // Modern Answer Submission State
            var selectedImageUris by remember { mutableStateOf<List<String>>(emptyList()) }
            var selectedPdfUri by remember { mutableStateOf<String?>(null) }
            var selectedAudioUri by remember { mutableStateOf<String?>(null) }

            var userRating by remember(post.id) { mutableFloatStateOf(uiState.userRating) }
            var showDeleteDialog by remember { mutableStateOf(false) }

            // Full Screen Image Dialog State
            var fullScreenImageUrl by remember { mutableStateOf<String?>(null) }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = {
                        Text(text = "Delete Post", fontWeight = FontWeight.Bold)
                    },
                    text = {
                        Text(text = "Are you sure you want to delete this post? This action cannot be undone.")
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDeleteDialog = false
                                onDeleteClick()
                            }
                        ) {
                            Text("Delete", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Cancel")
                        }
                    },
                )
            }

            if (fullScreenImageUrl != null) {
                Dialog(
                    onDismissRequest = { fullScreenImageUrl = null },
                    properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.9f))
                            .clickable { fullScreenImageUrl = null },
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = fullScreenImageUrl,
                            contentDescription = "Full Screen Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                        IconButton(
                            onClick = { fullScreenImageUrl = null },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp)
                                .statusBarsPadding()
                        ) {
                            Icon(
                                imageVector = KithIcons.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            Scaffold(
                modifier = modifier.fillMaxSize(),
                containerColor = extendedColors.detailBg,
                topBar = {
                    PostDetailTopBar(
                        onBackClick = onBackClick,
                        extendedColors = extendedColors,
                        showDeleteButton = uiState.isAuthor,
                        onDeleteClick = { showDeleteDialog = true },
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
                                    val filesToSubmit = mutableListOf<AttachedFile>()
                                    selectedImageUris.forEachIndexed { index, uri ->
                                        filesToSubmit.add(AttachedFile(name = "image_$index.webp", size = "", isDocument = false, uri = uri))
                                    }
                                    selectedPdfUri?.let { uri ->
                                        filesToSubmit.add(AttachedFile(name = "document.pdf", size = "", isDocument = true, uri = uri))
                                    }
                                    selectedAudioUri?.let { uri ->
                                        filesToSubmit.add(AttachedFile(name = "audio.m4a", size = "", isDocument = false, uri = uri))
                                    }

                                    onSubmitAnswer(answerText, filesToSubmit)
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
                    AuthorProfileCard(
                        author = post.author,
                        roleLabel = "ASKER",
                        extendedColors = extendedColors,
                        onClick = {
                            if (uiState.isAuthor) {
                                onNavigateToMyProfile()
                            } else {
                                onAuthorClick(post.author.id)
                            }
                        }
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = post.title,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = extendedColors.detailTextPrimary,
                            lineHeight = 28.sp,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = { isAttachmentsExpanded = !isAttachmentsExpanded },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isAttachmentsExpanded) KithIcons.ArrowDropUp else KithIcons.ArrowDropDown,
                                contentDescription = if (isAttachmentsExpanded) "Collapse details" else "Expand details",
                                tint = extendedColors.detailTextPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    PostTagsRow(
                        isInPerson = post.isInPerson,
                        communityName = post.community.name,
                        extendedColors = extendedColors,
                        onCommunityClick = { onCommunityClick(post.community.id) },
                    )

                    AnimatedVisibility(
                        visible = isAttachmentsExpanded,
                        enter = expandVertically(animationSpec = tween(300)) + fadeIn(),
                        exit = shrinkVertically(animationSpec = tween(300)) + fadeOut()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            if (post.content.isNotBlank()) {
                                Text(
                                    text = post.content,
                                    fontSize = 14.5.sp,
                                    color = extendedColors.detailTextSecondary,
                                    lineHeight = 21.sp
                                )
                            }

                            // Author Attachments
                            if (uiState.authorImageUris.isNotEmpty()) {
                                PostMediaAttachments(
                                    images = uiState.authorImageUris,
                                    onImageClick = { url -> fullScreenImageUrl = url }
                                )
                            }

                            if (uiState.authorPdfUri != null || uiState.authorAudioUri != null) {
                                DocumentLinksRow(
                                    pdfUri = uiState.authorPdfUri,
                                    audioUri = uiState.authorAudioUri
                                )
                            }
                        }
                    }

                    val currentAnswer = post.answer
                    if (isAnswerReceived && !currentAnswer.isNullOrBlank()) {
                        SolutionCard(
                            solution = currentAnswer,
                            solver = post.solver,
                            imageUris = uiState.solverImageUris,
                            pdfUri = uiState.solverPdfUri,
                            audioUri = uiState.solverAudioUri,
                            isAuthor = uiState.isAuthor, // PASSED
                            status = uiState.solutionStatus, // PASSED
                            rating = userRating, // PASSED
                            extendedColors = extendedColors,
                            onAccept = onAcceptSolution, // PASSED
                            onReject = onRejectSolution, // PASSED
                            onRatingChange = { newRating -> // PASSED
                                userRating = newRating
                                onRateSolution(newRating)
                            },
                            onImageClick = { url -> fullScreenImageUrl = url },
                            onSolverClick = {
                                if (uiState.isAcceptedByCurrentUser) {
                                    onNavigateToMyProfile()
                                } else {
                                    post.solver?.id?.let { onAuthorClick(it) }
                                }
                            }
                        )
//                        if (uiState.isAuthor) {
//                            RateSolutionCard(
//                                rating = userRating,
//                                onRatingChange = { newRating ->
//                                    userRating = newRating
//                                    onRateSolution(newRating)
//                                }
//                            )
//
//                            SolutionDecisionSection(
//                                status = uiState.solutionStatus,
//                                onAccept = onAcceptSolution,
//                                onReject = onRejectSolution
//                            )
//                        }

                        BountyRewardCard(
                            reward = post.reward,
                            extendedColors = extendedColors,
                            isCompleted = true,
                        )
                    } else if (!uiState.isAuthor && uiState.isAcceptedByCurrentUser) {
                        BountyRewardCard(
                            reward = post.reward,
                            extendedColors = extendedColors,
                            isCompleted = false,
                        )

                        YourAnswerSectionModern(
                            answerText = answerText,
                            onAnswerChange = { answerText = it },
                            extendedColors = extendedColors,
                            selectedImageUris = selectedImageUris,
                            selectedPdfUri = selectedPdfUri,
                            selectedAudioUri = selectedAudioUri,
                            onLaunchPhotoPicker = {
                                if (selectedImageUris.size < 5) {
                                    mediaPicker.launchPhotoPicker { uris ->
                                        val slotsLeft = 5 - selectedImageUris.size
                                        val newUris = uris.take(slotsLeft).map { it.toString() }
                                        selectedImageUris = selectedImageUris + newUris
                                    }
                                }
                            },
                            onLaunchPdfPicker = {
                                mediaPicker.launchPdfPicker { uri ->
                                    if (uri != null) selectedPdfUri = uri.toString()
                                }
                            },
                            onLaunchAudioPicker = {
                                mediaPicker.launchAudioPicker { uri ->
                                    if (uri != null) selectedAudioUri = uri.toString()
                                }
                            },
                            onRemoveImage = { index ->
                                val newList = selectedImageUris.toMutableList().apply { removeAt(index) }
                                selectedImageUris = newList
                            },
                            onClearPdf = { selectedPdfUri = null },
                            onClearAudio = { selectedAudioUri = null },
                            onImageClick = { url -> fullScreenImageUrl = url }
                        )
                    } else {
                        BountyRewardCard(
                            reward = post.reward,
                            extendedColors = extendedColors,
                            isCompleted = false,
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
private fun PostDetailTopBar(
    onBackClick: () -> Unit,
    extendedColors: KithExtendedColors,
    modifier: Modifier = Modifier,
    showDeleteButton: Boolean = false,
    onDeleteClick: () -> Unit = {},
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
                tint = extendedColors.detailTextPrimary
            )
        }

        if (showDeleteButton) {
            IconButton(onClick = onDeleteClick, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = KithIcons.Delete,
                    contentDescription = "Delete Post",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun RateSolutionSection(
    rating: Float,
    extendedColors: KithExtendedColors,
    onRatingChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "How helpful was this solution?",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = extendedColors.detailTextPrimary,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            for (i in 1..5) {
                val isSelected = i <= rating
                val starScale by animateFloatAsState(
                    targetValue = if (isSelected) 1.2f else 1f,
                    animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f),
                    label = "star_scale"
                )

                Icon(
                    // UPDATED: Swap between filled and outlined icons based on selection
                    imageVector = if (isSelected) KithIcons.StarRate else KithIcons.StarRateOutlined,
                    contentDescription = "Rate $i stars",
                    tint = if (isSelected) extendedColors.detailStar else Color(0xFF9CA3AF),
                    modifier = Modifier
                        .size(32.dp)
                        .scale(starScale)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) {
                            onRatingChange(i.toFloat())
                        }
                )
            }
        }
    }
}

@Composable
private fun SolutionDecisionSection(
    status: SolutionStatus,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        AnimatedVisibility(
            visible = status == SolutionStatus.ACCEPTED,
            enter = scaleIn(spring(dampingRatio = 0.6f, stiffness = 400f)) + fadeIn(),
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFECFDF5),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF10B981))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = KithIcons.Check,
                        contentDescription = null,
                        tint = Color(0xFF059669),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Solution Accepted!",
                        color = Color(0xFF065F46),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    )
                }
            }
        }

        if (status == SolutionStatus.PENDING) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // UPDATED: Standard M3 Expressive Button (removes custom green)
                Button(
                    onClick = onAccept,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = KithIcons.Check,
                        contentDescription = null,
                        tint = androidx.compose.material3.LocalContentColor.current, // Overrides the hardcoded green in CheckIcon
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Accept Solution", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                // UPDATED: Standard M3 Expressive OutlinedButton (removes custom red)
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Reject this answer", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        if (status == SolutionStatus.REJECTED) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFFEF2F2),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFFCA5A5))
            ) {
                Text(
                    text = "You rejected this solution.",
                    color = Color(0xFF991B1B),
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun AuthorProfileCard(
    author: User,
    onClick: () -> Unit,
    extendedColors: KithExtendedColors,
    modifier: Modifier = Modifier,
    roleLabel: String? = null,
    containerColor: Color? = null,
    contentPadding: PaddingValues = PaddingValues(14.dp)
) {
    val actualBgColor = containerColor ?: extendedColors.detailCardBg

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = actualBgColor),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            com.kith.core.ui.ProfileAvatar(
                user = author,
                avatarSize = 52.dp,
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                if (roleLabel != null) {
                    Text(
                        text = roleLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = extendedColors.detailPrimaryBlue,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
                Text(
                    text = author.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = extendedColors.detailTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = KithIcons.StarRate,
                        contentDescription = "Rating",
                        tint = extendedColors.detailStar,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${author.rating}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = extendedColors.detailTextSecondary,
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
    extendedColors: KithExtendedColors,
    onCommunityClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(shape = RoundedCornerShape(50), color = extendedColors.detailTagBg) {
            Text(
                text = if (isInPerson) "In-Person" else "Virtual",
                color = extendedColors.detailTagText,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            )
        }
        if (communityName.isNotBlank()) {
            Surface(
                shape = RoundedCornerShape(50),
                color = extendedColors.detailTagBg,
                modifier = Modifier.clickable(onClick = onCommunityClick)
            ) {
                Text(
                    text = communityName,
                    color = extendedColors.detailTagText,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PostMediaAttachments(
    modifier: Modifier = Modifier,
    images: List<String>,
    onImageClick: (String) -> Unit = {}
) {
    if (images.isNotEmpty()) {
        val carouselState = rememberCarouselState { images.size }
        HorizontalCenteredHeroCarousel(
            state = carouselState,
            itemSpacing = 8.dp,
            modifier = modifier
                .fillMaxWidth()
                .height(130.dp),
        ) { index ->
            Card(
                modifier = Modifier
                    .maskClip(RoundedCornerShape(16.dp))
                    .clickable { onImageClick(images[index]) },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                AsyncImage(
                    model = images[index],
                    contentDescription = "Selected Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
private fun SolutionCard(
    solution: String,
    solver: User?,
    imageUris: List<String>,
    pdfUri: String?,
    audioUri: String?,
    isAuthor: Boolean,
    status: SolutionStatus,
    rating: Float,
    extendedColors: KithExtendedColors,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onRatingChange: (Float) -> Unit,
    onImageClick: (String) -> Unit,
    onSolverClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = extendedColors.detailSolutionBg),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (solver != null) {
                AuthorProfileCard(
                    author = solver,
                    roleLabel = "SOLVER",
                    extendedColors = extendedColors,
                    onClick = onSolverClick,
                    containerColor = Color.Transparent,
                    contentPadding = PaddingValues(0.dp)
                )
                HorizontalDivider(color = extendedColors.detailBorderLight)
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Solution",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = extendedColors.detailTextPrimary
                )
                Text(
                    text = solution,
                    fontSize = 14.sp,
                    color = extendedColors.detailTextSecondary,
                    lineHeight = 21.sp
                )
            }

            if (imageUris.isNotEmpty()) {
                PostMediaAttachments(
                    images = imageUris,
                    onImageClick = onImageClick
                )
            }

            if (pdfUri != null || audioUri != null) {
                DocumentLinksRow(pdfUri = pdfUri, audioUri = audioUri)
            }

            // ADDED: Inline author actions
            if (isAuthor) {
                HorizontalDivider(color = extendedColors.detailBorderLight)

                if (status == SolutionStatus.PENDING) {
                    RateSolutionSection(
                        rating = rating,
                        extendedColors = extendedColors,
                        onRatingChange = onRatingChange
                    )
                }

                SolutionDecisionSection(
                    status = status,
                    onAccept = onAccept,
                    onReject = onReject
                )
            }
        }
    }
}

@Composable
private fun BountyRewardCard(
    reward: Int,
    extendedColors: KithExtendedColors,
    isCompleted: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = extendedColors.detailBountyBg),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.2.dp, extendedColors.detailBountyBorder),
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
                color = if (isCompleted) extendedColors.detailBountyXp else extendedColors.detailTextPrimary,
            )
            Text(
                text = "$reward XP",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PostDetailColors.BountyXp,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun YourAnswerSectionModern(
    answerText: String,
    onAnswerChange: (String) -> Unit,
    selectedImageUris: List<String>,
    selectedPdfUri: String?,
    selectedAudioUri: String?,
    extendedColors: KithExtendedColors,
    onLaunchPhotoPicker: () -> Unit,
    onLaunchPdfPicker: () -> Unit,
    onLaunchAudioPicker: () -> Unit,
    onRemoveImage: (Int) -> Unit,
    onClearPdf: () -> Unit,
    onClearAudio: () -> Unit,
    onImageClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Your Answer",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = extendedColors.detailTextPrimary,
            )
            OutlinedTextField(
                value = answerText,
                onValueChange = onAnswerChange,
                placeholder = {
                    Text(
                        "Provide details or instructions...",
                        fontWeight = FontWeight.Normal,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal),
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                maxLines = 5,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    focusedBorderColor = extendedColors.detailPrimaryBlue,
                )
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "ATTACHMENTS",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = extendedColors.detailTextSecondary,
                letterSpacing = 1.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Using standard material defaults matching CreatePostScreen's DocumentCard
                DocumentCard(
                    icon = KithIcons.Photo,
                    label = "Photo",
                    badgeText = if (selectedImageUris.isNotEmpty()) "${selectedImageUris.size}/5" else "Add",
                    modifier = Modifier.weight(1f),
                    onClick = onLaunchPhotoPicker
                )
                DocumentCard(
                    icon = KithIcons.Description,
                    label = "PDF",
                    badgeText = if (selectedPdfUri != null) "1/1" else "Docs",
                    modifier = Modifier.weight(1f),
                    showClearButton = selectedPdfUri != null,
                    onClear = onClearPdf,
                    onClick = onLaunchPdfPicker
                )
                DocumentCard(
                    icon = KithIcons.AudioFile,
                    label = "Audio",
                    badgeText = if (selectedAudioUri != null) "1/1" else "Voice",
                    modifier = Modifier.weight(1f),
                    showClearButton = selectedAudioUri != null,
                    onClear = onClearAudio,
                    onClick = onLaunchAudioPicker
                )
            }
        }

        if (selectedImageUris.isNotEmpty()) {
            val carouselState = rememberCarouselState { selectedImageUris.size }
            HorizontalCenteredHeroCarousel(
                state = carouselState,
                itemSpacing = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
            ) { index ->
                Card(
                    modifier = Modifier
                        .maskClip(RoundedCornerShape(16.dp))
                        .clickable { onImageClick(selectedImageUris[index]) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = selectedImageUris[index],
                            contentDescription = "Selected Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        Surface(
                            onClick = { onRemoveImage(index) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(6.dp)
                                .size(24.dp),
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.5f),
                            contentColor = Color.White
                        ) {
                            Icon(
                                imageVector = KithIcons.Close,
                                contentDescription = "Remove Image",
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Minimal implementation of DocumentCard if imported from CreatePost
@Composable
fun DocumentCard(
    icon: ImageVector,
    label: String,
    badgeText: String,
    modifier: Modifier = Modifier,
    showClearButton: Boolean = false,
    onClear: (() -> Unit)? = null,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier.height(84.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = null,
        onClick = onClick,
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(10.dp)) {
            if (showClearButton && onClear != null) {
                Surface(
                    onClick = { onClear() },
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f),
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(20.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = KithIcons.Close,
                            contentDescription = "Clear Selection",
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

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
        val animatedStartColor by animateColorAsState(
            targetValue = if (isAccepted) PostDetailColors.AcceptedGradientStart else PostDetailColors.GradientStart,
            animationSpec = tween(400),
            label = "gradient_start"
        )
        val animatedEndColor by animateColorAsState(
            targetValue = if (isAccepted) PostDetailColors.AcceptedGradientEnd else PostDetailColors.GradientEnd,
            animationSpec = tween(400),
            label = "gradient_end"
        )
        val animatedShadowColor by animateColorAsState(
            targetValue = if (isAccepted) Color(0x5010B981) else Color(0x351E5FF5),
            animationSpec = tween(400),
            label = "shadow_color"
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
            modifier = Modifier
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

@Composable
private fun DocumentLinksRow(
    pdfUri: String?,
    audioUri: String?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (pdfUri != null) {
            Surface(
                onClick = {
                    try {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(pdfUri)))
                    } catch (e: Exception) {
                        Toast.makeText(context, "No app found to open PDF", Toast.LENGTH_SHORT).show()
                    }
                },
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(KithIcons.Description, contentDescription = null, tint = PostDetailColors.PrimaryBlue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("View PDF", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }
        }

        if (audioUri != null) {
            Surface(
                onClick = {
                    try {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(audioUri)))
                    } catch (e: Exception) {
                        Toast.makeText(context, "No app found to play audio", Toast.LENGTH_SHORT).show()
                    }
                },
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(KithIcons.AudioFile, contentDescription = null, tint = PostDetailColors.PrimaryBlue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Play Audio", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }
        }
    }
}