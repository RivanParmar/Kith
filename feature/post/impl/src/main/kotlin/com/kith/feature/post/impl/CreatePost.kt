package com.kith.feature.post.impl

import android.util.Log
//import androidx.activity.result.PickVisualMediaRequest
//import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kith.core.designsystem.icon.KithIcons

@Composable
fun CreatePostScreen(
    modifier: Modifier = Modifier,
    viewModel: CreatePostViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
) {
    // Safely collect state from ViewModel
    val communitiesUiState by viewModel.communitiesUiState.collectAsStateWithLifecycle()
    val submissionState by viewModel.submissionState.collectAsStateWithLifecycle()

    // Handle navigation side effect strictly once upon success
    LaunchedEffect(submissionState) {
        if (submissionState is PostSubmissionState.Success) {
            onNavigateBack()
            viewModel.dismissSubmissionError()
        }
    }

    CreatePostScreen(
        modifier = modifier,
        communitiesUiState = communitiesUiState,
        onCancel = onNavigateBack,
        onPost = viewModel::createPost,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreatePostScreen(
    modifier: Modifier = Modifier,
    communitiesUiState: CommunitiesUiState,
    onCancel: () -> Unit = {},
    onPost: (Boolean, CreatePostFormState) -> Unit = { _, _ -> },
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Create Post",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onCancel) {
                        Text("Cancel")
                    }
                },
                actions = {
                    TextButton(onClick = { /* Handle preview */ }) {
                        Text("Preview", fontWeight = FontWeight.Bold)
                    }
                }
            )
        },
    ) { paddingValues ->
        var title by rememberSaveable { mutableStateOf("") }
        var description by rememberSaveable { mutableStateOf("") }
        var selectedReward by rememberSaveable { mutableStateOf(PostReward.EASY) }
        var selectedCommunity by rememberSaveable { mutableStateOf("") }
        var inPerson by rememberSaveable { mutableStateOf(false) }

        var selectedImageUris by remember { mutableStateOf<List<String>>(emptyList()) }
        var selectedPdfUri by remember { mutableStateOf<String?>(null) }
        var selectedAudioUri by remember { mutableStateOf<String?>(null) }

//        val photoPickerLauncher = rememberLauncherForActivityResult(
//            contract = ActivityResultContracts.PickVisualMedia(),
//            onResult = { uri ->
//                if (uri != null) {
//                    selectedImageUris = selectedImageUris + uri.toString()
//                }
//            }
//        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    placeholder = { Text("Post headline...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    placeholder = { Text("Details and story...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(12.dp),
                )

                val carouselState = rememberCarouselState { 5 }

                Button(
                    onClick = {
//                        photoPickerLauncher.launch(
//                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
//                        )
                    }
                ) {
                    Text("Pick Test Image")
                }

                if (selectedImageUris.isNotEmpty()) {
                    val carouselState = rememberCarouselState { selectedImageUris.size }

                    HorizontalCenteredHeroCarousel(
                        state = carouselState,
                        itemSpacing = 8.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                    ) { index ->
                        val imageUri = selectedImageUris[index]

                        Card(
                            modifier = Modifier.maskClip(RoundedCornerShape(16.dp)),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                            )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                // If you have Coil installed, you can use AsyncImage to actually see it:
                                /*
                                AsyncImage(
                                    model = imageUri,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                */

                                // Otherwise, just print the URI to prove it worked:
                                Text(
                                    text = "Loaded URI:\n$imageUri",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(12.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    DocumentCard(
                        icon = KithIcons.Settings, // Replace with your actual document icon
                        label = "Add PDF (1/1)",
                        modifier = Modifier.weight(1f),
                    )
                    DocumentCard(
                        icon = KithIcons.Visibility, // Replace with your actual audio icon
                        label = "Add Audio (1/1)",
                        modifier = Modifier.weight(1f),
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "DIFFICULTY",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        PostReward.entries.forEach { reward ->
                            SegmentedButton(
                                selected = reward == selectedReward,
                                onClick = { selectedReward = reward },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = reward.ordinal,
                                    count = PostReward.entries.size,
                                )
                            ) {
                                Text(reward.title) // Assuming you have a .title property
                            }
                        }
                    }
                }

                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedCommunity.ifEmpty { "SELECT COMMUNITY" },
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        // Dynamically display based on ViewModel state
                        when (communitiesUiState) {
                            is CommunitiesUiState.Loading -> {
                                DropdownMenuItem(
                                    text = { Text("Loading...") },
                                    onClick = { expanded = false }
                                )
                            }

                            is CommunitiesUiState.Error -> {
                                DropdownMenuItem(
                                    text = { Text("Failed to load communities") },
                                    onClick = { expanded = false }
                                )
                            }

                            is CommunitiesUiState.Success -> {
                                communitiesUiState.communities.forEach { community ->
                                    DropdownMenuItem(
                                        text = { Text(community.name) },
                                        onClick = {
                                            selectedCommunity = community.name
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { inPerson = !inPerson }
                        .padding(vertical = 4.dp),
                ) {
                    Checkbox(
                        checked = inPerson,
                        onCheckedChange = { inPerson = it },
                    )
                    Text(
                        text = "MARK AS IN-PERSON POST",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }

                // Extra spacer so the bottom items are not hidden by the floating button
                Spacer(modifier = Modifier.height(88.dp))
            }

            FloatingSplitButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .padding(bottom = 16.dp),
                onPost = { isDraft ->
                    // Bundle all the local UI state into the data class when the button is clicked
                    val currentFormState = CreatePostFormState(
                        title = title,
                        content = description,
                        reward = selectedReward,
                        selectedCommunity = selectedCommunity,
                        isInPerson = inPerson,
                        selectedImageUris = selectedImageUris,
                        selectedPdfUri = selectedPdfUri,
                        selectedAudioUri = selectedAudioUri
                    )
                    onPost(isDraft, currentFormState)
                },
            )
        }
    }
}

@Composable
fun DocumentCard(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.height(64.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        onClick = { /* Handle document attach */ },
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FloatingSplitButton(
    modifier: Modifier = Modifier,
    onPost: (Boolean) -> Unit = { _ -> },
) {
    var checked by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        SplitButtonLayout(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(56.dp),
            leadingButton = {
                SplitButtonDefaults.LeadingButton(
                    onClick = {
                        onPost(false)
                        Log.d("CREATE", "Clicked")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "POST",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            },
            trailingButton = {
                SplitButtonDefaults.TrailingButton(
                    checked = checked,
                    onCheckedChange = { checked = it },
                    modifier = Modifier.height(56.dp)
                ) {
                    val rotation: Float by animateFloatAsState(
                        targetValue = if (checked) 180f else 0f,
                        label = "Trailing Icon Rotation"
                    )
                    Icon(
                        imageVector = KithIcons.Settings, // Ensure this maps to a dropdown arrow icon
                        contentDescription = "More Options",
                        modifier = Modifier.rotate(rotation)
                    )
                }
            }
        )

        DropdownMenu(
            expanded = checked,
            onDismissRequest = { checked = false }
        ) {
            DropdownMenuItem(
                text = { Text("Save to Draft") },
                onClick = { onPost(true) }
            )
        }
    }
}