package com.kith.feature.post.impl

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.kith.core.designsystem.icon.KithIcons
import com.kith.core.designsystem.theme.KithTheme
import com.kith.core.model.data.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MediaPickerHelper(
    val launchPhotoPicker: (onResult: (List<Uri>) -> Unit) -> Unit,
    val launchPdfPicker: (onResult: (Uri?) -> Unit) -> Unit,
    val launchAudioPicker: (onResult: (Uri?) -> Unit) -> Unit,
)

val LocalMediaPickerHelper = staticCompositionLocalOf<MediaPickerHelper> {
    error("No MediaPickerHelper provided. Ensure it is provided in KithApp.kt")
}

// Strict word counter for logic
private fun String.effectiveWordCount(): Int {
    if (this.isBlank()) return 0
    val count = this.trim().split("\\s+".toRegex()).size
    return if (this.last().isWhitespace()) count + 1 else count
}

// Standard word counter for display
private fun String.wordCount(): Int = if (this.isBlank()) 0 else this.trim().split("\\s+".toRegex()).size

@Composable
fun CreatePostScreen(
    modifier: Modifier = Modifier,
    viewModel: CreatePostViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
) {
    val communitiesUiState by viewModel.communitiesUiState.collectAsStateWithLifecycle()
    val submissionState by viewModel.submissionState.collectAsStateWithLifecycle()
    val userSearchResults by viewModel.userSearchResults.collectAsStateWithLifecycle()
    val isPremium by viewModel.isPremium.collectAsStateWithLifecycle()

    LaunchedEffect(submissionState) {
        if (submissionState is PostSubmissionState.Success) {
            delay(1500) // The delay allows the success animation to play fully
            onNavigateBack()
            viewModel.dismissSubmissionError()
        }
    }

    CreatePostScreen(
        modifier = modifier,
        communitiesUiState = communitiesUiState,
        userSearchResults = userSearchResults,
        submissionState = submissionState,
        isPremium = isPremium,
        onSearchUsers = viewModel::searchUsersInCommunity,
        onClearSearch = viewModel::clearUserSearch,
        onCancel = onNavigateBack,
        onPost = viewModel::createPost,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreatePostScreen(
    modifier: Modifier = Modifier,
    communitiesUiState: CommunitiesUiState,
    userSearchResults: List<User> = emptyList(),
    submissionState: PostSubmissionState = PostSubmissionState.Idle,
    isPremium: Boolean = false,
    onSearchUsers: (communityId: String, query: String) -> Unit = { _, _ -> },
    onClearSearch: () -> Unit = {},
    onCancel: () -> Unit = {},
    onPost: (CreatePostFormState) -> Unit = { _ -> },
) {
    // Fetch the Media Picker Helper provided by KithApp.kt
    val mediaPicker = LocalMediaPickerHelper.current

    var formState by remember { mutableStateOf(CreatePostFormState()) }
    var userSearchQuery by rememberSaveable { mutableStateOf("") }

    var titleError by rememberSaveable { mutableStateOf<String?>(null) }
    var descriptionError by rememberSaveable { mutableStateOf<String?>(null) }
    var communityError by rememberSaveable { mutableStateOf<String?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.surface,
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    title = {
                        Text(
                            text = "Create Post",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },

                )
            },
        ) { paddingValues ->
            val scrollState = rememberScrollState()
            val coroutineScope = rememberCoroutineScope()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(22.dp)
                ) {
                    Spacer(modifier = Modifier.height(4.dp))

                    // Title Input
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        OutlinedTextField(
                            value = formState.title,
                            onValueChange = { newText ->
                                val maxWords = 20
                                if (newText.effectiveWordCount() <= maxWords) {
                                    if (newText != formState.title) {
                                        formState = formState.copy(title = newText)
                                        titleError = null
                                    }
                                } else {
                                    titleError = "Limit reached ($maxWords words)"
                                }
                            },
                            placeholder = {
                                Text(
                                    "What do you need help with?",
                                    fontWeight = FontWeight.Normal,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            },
                            textStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            isError = titleError != null,
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(
                                    alpha = 0.5f
                                ),
                                focusedBorderColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = titleError ?: "",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "${formState.title.wordCount()}/20 words",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Normal,
                                color = if (titleError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Description Input
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        OutlinedTextField(
                            value = formState.content,
                            onValueChange = { newText ->
                                val maxWords = 500
                                if (newText.effectiveWordCount() <= maxWords) {
                                    if (newText != formState.content) {
                                        formState = formState.copy(content = newText)
                                        descriptionError = null
                                    }
                                } else {
                                    descriptionError = "Limit reached ($maxWords words)"
                                }
                            },
                            placeholder = {
                                Text(
                                    "Provide details, instructions, or story...",
                                    fontWeight = FontWeight.Normal,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal),
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 4,
                            maxLines = 5,
                            shape = RoundedCornerShape(14.dp),
                            isError = descriptionError != null,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(
                                    alpha = 0.5f
                                ),
                                focusedBorderColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = descriptionError ?: "",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "${formState.content.wordCount()}/500 words",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Normal,
                                color = if (descriptionError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Attachments Section
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "ATTACHMENTS",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            DocumentCard(
                                icon = KithIcons.Settings,
                                label = "Photo",
                                badgeText = if (formState.selectedImageUris.isNotEmpty()) "${formState.selectedImageUris.size}/5" else "Add",
                                modifier = Modifier.weight(1f),
                                isPremiumFeature = false,
                                isUserPremium = true,
                                onClick = {
                                    if (formState.canAddMoreImages) {
                                        mediaPicker.launchPhotoPicker { uris ->
                                            if (uris.isNotEmpty()) {
                                                formState =
                                                    formState.copy(selectedImageUris = uris.map { it.toString() })
                                            }
                                        }
                                    }
                                }
                            )
                            DocumentCard(
                                icon = KithIcons.Settings,
                                label = "PDF",
                                badgeText = if (formState.selectedPdfUri != null) "1/1" else "Docs",
                                modifier = Modifier.weight(1f),
                                isPremiumFeature = true,
                                isUserPremium = isPremium,
                                showClearButton = formState.selectedPdfUri != null,
                                onClear = { formState = formState.copy(selectedPdfUri = null) },
                                onClick = {
                                    mediaPicker.launchPdfPicker { uri ->
                                        if (uri != null) {
                                            formState =
                                                formState.copy(selectedPdfUri = uri.toString())
                                        }
                                    }
                                }
                            )
                            DocumentCard(
                                icon = KithIcons.Visibility,
                                label = "Audio",
                                badgeText = if (formState.selectedAudioUri != null) "1/1" else "Voice",
                                modifier = Modifier.weight(1f),
                                isPremiumFeature = true,
                                isUserPremium = isPremium,
                                showClearButton = formState.selectedAudioUri != null,
                                onClear = { formState = formState.copy(selectedAudioUri = null) },
                                onClick = {
                                    mediaPicker.launchAudioPicker { uri ->
                                        if (uri != null) {
                                            formState =
                                                formState.copy(selectedAudioUri = uri.toString())
                                        }
                                    }
                                }
                            )
                        }
                    }

                    // Carousel with AsyncImage & Remove Buttons
                    if (formState.selectedImageUris.isNotEmpty()) {
                        val carouselState =
                            rememberCarouselState { formState.selectedImageUris.size }
                        HorizontalCenteredHeroCarousel(
                            state = carouselState,
                            itemSpacing = 8.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp),
                        ) { index ->
                            Card(
                                modifier = Modifier.maskClip(RoundedCornerShape(16.dp)),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    AsyncImage(
                                        model = formState.selectedImageUris[index],
                                        contentDescription = "Selected Image",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )

                                    // Cross button to remove image from carousel
                                    Surface(
                                        onClick = {
                                            val newList =
                                                formState.selectedImageUris.toMutableList()
                                                    .apply { removeAt(index) }
                                            formState = formState.copy(selectedImageUris = newList)
                                        },
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

                    // Difficulty / Reward Selection
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "TASK DIFFICULTY",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp
                        )

                        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                            PostReward.entries.forEach { reward ->
                                SegmentedButton(
                                    selected = reward == formState.reward,
                                    onClick = { formState = formState.copy(reward = reward) },
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = reward.ordinal,
                                        count = PostReward.entries.size,
                                    )
                                ) {
                                    Text(reward.title, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Audience / Destination Section
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "TARGET DESTINATION",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp
                        )

                        var expandedCommunity by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expandedCommunity,
                            onExpandedChange = { expandedCommunity = it },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = formState.selectedCommunity?.name ?: "",
                                onValueChange = {},
                                readOnly = true,
                                placeholder = { Text("Select Community") },
                                label = { Text("Community", fontWeight = FontWeight.Normal) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCommunity) },
                                modifier = Modifier
                                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                isError = communityError != null,
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedBorderColor = MaterialTheme.colorScheme.primary
                                ),
                                supportingText = communityError?.let { error ->
                                    {
                                        Text(
                                            text = error,
                                            fontWeight = FontWeight.Normal,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            )
                            ExposedDropdownMenu(
                                expanded = expandedCommunity,
                                onDismissRequest = { expandedCommunity = false }
                            ) {
                                when (communitiesUiState) {
                                    is CommunitiesUiState.Loading -> {
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    "Loading communities...",
                                                    fontWeight = FontWeight.Normal
                                                )
                                            },
                                            onClick = { expandedCommunity = false }
                                        )
                                    }

                                    is CommunitiesUiState.Error -> {
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    "Failed to load",
                                                    fontWeight = FontWeight.Normal
                                                )
                                            },
                                            onClick = { expandedCommunity = false }
                                        )
                                    }

                                    is CommunitiesUiState.Success -> {
                                        communitiesUiState.communities.forEach { community ->
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        community.name,
                                                        fontWeight = FontWeight.Normal
                                                    )
                                                },
                                                onClick = {
                                                    formState = formState.copy(
                                                        selectedCommunity = community,
                                                        selectedTargetUser = null
                                                    )
                                                    userSearchQuery = ""
                                                    communityError = null
                                                    expandedCommunity = false
                                                    onClearSearch()
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Direct User Autocomplete
                        var expandedUser by remember { mutableStateOf(false) }

                        ExposedDropdownMenuBox(
                            expanded = expandedUser && isPremium && userSearchResults.isNotEmpty(),
                            onExpandedChange = { if (isPremium) expandedUser = it },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = userSearchQuery,
                                onValueChange = {
                                    if (isPremium) {
                                        userSearchQuery = it
                                        if (formState.selectedTargetUser != null) {
                                            formState = formState.copy(selectedTargetUser = null)
                                        }
                                        expandedUser = true

                                        if (formState.selectedCommunity != null) {
                                            onSearchUsers(formState.selectedCommunity!!.id, it)
                                        }
                                    }
                                },
                                readOnly = !isPremium,
                                enabled = isPremium,
                                placeholder = {
                                    Text(
                                        text = if (isPremium) "Assign directly to user (Optional)" else "Direct Send (Premium Only)",
                                        fontWeight = FontWeight.Normal
                                    )
                                },
                                label = {
                                    Text(
                                        "Direct Recipient",
                                        fontWeight = FontWeight.Normal
                                    )
                                },
                                trailingIcon = {
                                    if (!isPremium) {
                                        Surface(
                                            color = Color(0xFFFFD700).copy(alpha = 0.2f),
                                            shape = CircleShape,
                                            modifier = Modifier
                                                .padding(end = 12.dp)
                                                .size(18.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = KithIcons.Crown,
                                                    contentDescription = "PRO",
                                                    tint = Color(0xFFD97706),
                                                    modifier = Modifier.size(11.dp)
                                                )
                                            }
                                        }
                                    } else {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedUser && isPremium)
                                    }
                                },
                                modifier = Modifier
                                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest.copy(
                                        alpha = 0.6f
                                    ),
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    disabledBorderColor = Color.Transparent
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = expandedUser && isPremium && userSearchResults.isNotEmpty(),
                                onDismissRequest = { expandedUser = false }
                            ) {
                                userSearchResults.forEach { user ->
                                    DropdownMenuItem(
                                        text = { Text(user.name, fontWeight = FontWeight.Normal) },
                                        onClick = {
                                            formState = formState.copy(selectedTargetUser = user)
                                            userSearchQuery = user.name
                                            expandedUser = false
                                            onClearSearch()
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // In-Person Toggle Row
                    Surface(
                        onClick = {
                            formState = formState.copy(isInPerson = !formState.isInPerson)
                        },
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Physical / In-Person",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Requires meeting locally on campus",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = formState.isInPerson,
                                onCheckedChange = { formState = formState.copy(isInPerson = it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }

                    FloatingSplitButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 32.dp),
                        onPost = {
                            var isLocalValid = true

                            if (formState.title.isBlank()) {
                                titleError = "Title cannot be empty"
                                isLocalValid = false
                            }

                            if (formState.content.isBlank()) {
                                descriptionError = "Description cannot be empty"
                                isLocalValid = false
                            }

                            if (formState.selectedCommunity == null) {
                                communityError = "Please select a community"
                                isLocalValid = false
                            }

                            if (isLocalValid && formState.isValid) {
                                onPost(formState)
                                // FIX: A fresh class instance clears ALL fields completely (Images, PDF, Audio, Dropdowns, Toggles)
                                formState = CreatePostFormState()
                                userSearchQuery = ""
                            }
                        },
                    )
                }


                val showScrollIndicator by remember {
                    derivedStateOf {
                        scrollState.maxValue > 0 && scrollState.value < scrollState.maxValue - 50
                    }
                }

                AnimatedVisibility(
                    visible = showScrollIndicator,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp),
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
                ) {
                    Surface(
                        onClick = {
                            coroutineScope.launch {
                                scrollState.animateScrollTo(scrollState.maxValue)
                            }
                        },
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.95f),
                        modifier = Modifier.size(36.dp),
                        shadowElevation = 4.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = KithIcons.ArrowDown,
                                contentDescription = "Scroll down to bottom",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = submissionState is PostSubmissionState.Submitting || submissionState is PostSubmissionState.Success,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = submissionState is PostSubmissionState.Success,
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                                scaleIn(initialScale = 0.8f, animationSpec = tween(220, delayMillis = 90)))
                            .togetherWith(fadeOut(animationSpec = tween(90)))
                    },
                    label = "Success Overlay Animation"
                ) { isSuccess ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (isSuccess) {
                            val scale by animateFloatAsState(
                                targetValue = 1f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                ),
                                label = "tick_scale"
                            )

                            Icon(
                                imageVector = KithIcons.CheckCircle,
                                contentDescription = "Success",
                                tint = Color(0xFF10B981),
                                modifier = Modifier
                                    .size(100.dp)
                                    .scale(scale)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "Task Posted Successfully!",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        } else {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(64.dp),
                                strokeWidth = 6.dp
                            )
                            Spacer(modifier = Modifier.height(32.dp))
                            Text(
                                text = "Creating your task...",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DocumentCard(
    icon: ImageVector,
    label: String,
    badgeText: String,
    modifier: Modifier = Modifier,
    isPremiumFeature: Boolean = false,
    isUserPremium: Boolean = false,
    showClearButton: Boolean = false,
    onClear: (() -> Unit)? = null,
    onClick: () -> Unit = {}
) {
    val isLocked = isPremiumFeature && !isUserPremium

    Surface(
        modifier = modifier.height(84.dp),
        shape = RoundedCornerShape(16.dp),
        color = if (isLocked) MaterialTheme.colorScheme.surfaceContainerLowest else MaterialTheme.colorScheme.surfaceContainerLow,
        border = null,
        onClick = onClick,
        enabled = !isLocked
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(10.dp)) {
            if (showClearButton && onClear != null) {
                // Remove Button overlay for PDF/Audio
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
            } else if (isLocked) {
                Surface(
                    color = Color(0xFFFFD700).copy(alpha = 0.2f),
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(18.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = KithIcons.Crown,
                            contentDescription = "PRO",
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(11.dp)
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
                        .background(
                            if (isLocked) MaterialTheme.colorScheme.surfaceContainerHigh
                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (isLocked) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isLocked) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isLocked) "Locked" else badgeText,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Normal,
                        color = if (isLocked) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FloatingSplitButton(
    modifier: Modifier = Modifier,
    onPost: () -> Unit = {},
) {
    Box(modifier = modifier) {
        Button(
            onClick = onPost,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            elevation = ButtonDefaults.buttonElevation(0.dp)
        ) {
            Text(
                text = "POST TASK",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CreatePostScreenPreview() {
    KithTheme {
        CreatePostScreen(
            communitiesUiState = CommunitiesUiState.Loading,
            isPremium = false,
            onCancel = {},
            onPost = { _ -> }
        )
    }
}