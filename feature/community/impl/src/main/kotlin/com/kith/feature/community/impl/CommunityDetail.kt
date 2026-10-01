package com.kith.feature.community.impl

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.kith.core.designsystem.icon.KithIcons
import com.kith.core.designsystem.theme.KithExtendedColors
import com.kith.core.designsystem.theme.KithTheme

@Composable
fun CommunityDetailScreen(
    communityId: String,
    onBackClick: () -> Unit,
    onNavigateUp: () -> Unit,
) {
    val viewModel = hiltViewModel<CommunityDetailViewModel, CommunityDetailViewModel.Factory>(
        creationCallback = { factory ->
            factory.create(communityId)
        }
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val extendedColors = KithTheme.extendedColors

    when (uiState) {
        is CommunityDetailUiState.Loading -> { /* Loader */ }
        is CommunityDetailUiState.Success -> {
            val state = uiState as CommunityDetailUiState.Success
            CommunityDetailContent(
                communityName = state.community.name,
                communityImageUrl = state.community.imageUrl,
                initialDetail = state.community.description
                    ?: "Give details... What do you need help with or want to share?",
                isAdmin = state.isAdmin,
                extendedColors = extendedColors, // PASSED DOWN
                onBackClick = onBackClick,
                onLeaveOrDeleteClick = {
                    viewModel.leaveOrDeleteCommunity(
                        isAdmin = state.isAdmin,
                        onComplete = onNavigateUp
                    )
                },
                onDetailChange = viewModel::updateDescription,
                onImageClick = { uri ->
                    if (uri != null) {
                        viewModel.updateCommunityImage(uri.toString())
                    }
                }
            )
        }
    }
}

@Composable
internal fun CommunityDetailContent(
    communityName: String,
    communityImageUrl: String?,
    initialDetail: String,
    isAdmin: Boolean,
    extendedColors: KithExtendedColors,
    onBackClick: () -> Unit = {},
    onLeaveOrDeleteClick: () -> Unit = {},
    onDetailChange: (String) -> Unit = {},
    onImageClick: (Uri?) -> Unit = {},
) {
    val mediaPicker = LocalCommunityMediaPickerHelper.current
    var detailText by remember { mutableStateOf(initialDetail) }
    var isEditingDetail by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(extendedColors.communityBg) // Themed Background
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        IconButton(onClick = onBackClick, modifier = Modifier.padding(start = 0.dp)) {
            Icon(
                imageVector = KithIcons.ArrowBack,
                contentDescription = "Back",
                tint = extendedColors.communityIconSecondary // Themed Icon
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = communityName,
            color = extendedColors.communityTextPrimary, // Themed Text
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 36.sp
        )
        Spacer(modifier = Modifier.height(28.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(85.dp))
                    .background(extendedColors.communityImagePlaceholderBg) // Themed Placeholder Bg
                    .then(
                        if (isAdmin) Modifier.clickable {
                            mediaPicker.launchPhotoPicker { uri -> onImageClick(uri) }
                        } else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (!communityImageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = communityImageUrl,
                        contentDescription = "Community image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = KithIcons.Groups,
                        contentDescription = null,
                        tint = extendedColors.communityImagePlaceholderIcon, // Themed Placeholder Icon
                        modifier = Modifier.size(64.dp)
                    )
                }
            }
            if (isAdmin) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 18.dp, bottom = 6.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(extendedColors.communityCardBg) // Themed Edit Circle
                        .clickable {
                            mediaPicker.launchPhotoPicker { uri -> onImageClick(uri) }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = KithIcons.Edit,
                        contentDescription = "Edit community image",
                        tint = extendedColors.communityActionIcon, // Themed Edit Icon
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .border(BorderStroke(1.dp, extendedColors.communityCardBorder), RoundedCornerShape(14.dp)) // Themed Border
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Detail",
                        color = extendedColors.communityTextPrimary, // Themed Text
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (isAdmin) {
                        IconButton(
                            onClick = {
                                if (isEditingDetail) onDetailChange(detailText)
                                isEditingDetail = !isEditingDetail
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (isEditingDetail) KithIcons.Check else KithIcons.Edit,
                                contentDescription = "Edit detail",
                                tint = extendedColors.communityActionIcon, // Themed Action Icon
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                if (isAdmin && isEditingDetail) {
                    OutlinedTextField(
                        value = detailText,
                        onValueChange = { detailText = it },
                        minLines = 2, maxLines = 4,
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedBorderColor = extendedColors.communityCardBorder,
                            focusedBorderColor = extendedColors.communityActionIcon,
                            unfocusedTextColor = extendedColors.communityTextPrimary,
                            focusedTextColor = extendedColors.communityTextPrimary
                        )
                    )
                } else {
                    Text(
                        text = detailText,
                        color = extendedColors.communityTextSecondary, // Themed Text
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = onLeaveOrDeleteClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = extendedColors.communityDangerBg, // Themed Danger Bg
                contentColor = extendedColors.communityDangerText // Themed Danger Text
            )
        ) {
            Text(
                text = if (isAdmin) "Delete Community" else "Leave Community",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}