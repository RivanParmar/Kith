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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.kith.core.designsystem.theme.OutfitFontFamily
import com.kith.core.model.data.Community
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CommunitiesScreen(
    onCommunityClick: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    onJoinClick: () -> Unit = {},
    viewModel: CommunityListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val extendedColors = KithTheme.extendedColors

    Scaffold(containerColor = extendedColors.communityBg) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(extendedColors.communityBg) // Themed Background
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = KithIcons.ArrowBack,
                        contentDescription = "Back",
                        tint = extendedColors.communityTextPrimary // Themed Icon
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "Communities",
                    color = extendedColors.communityTextPrimary, // Themed Text
                    fontSize = 30.sp,
                    fontFamily = OutfitFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.weight(1f)
                )

                if (uiState is CommunityListUiState.Success) {
                    val state = uiState as CommunityListUiState.Success
                    if (state.isPremium) {
                        androidx.compose.material3.Surface(
                            onClick = onJoinClick,
                            shape = CircleShape,
                            color = extendedColors.communityActionBg, // Themed Action Bg
                            modifier = Modifier.size(40.dp).padding(end = 4.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = KithIcons.Add,
                                    contentDescription = "Join new community",
                                    tint = extendedColors.communityActionIcon, // Themed Action Icon
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }

            when (uiState) {
                is CommunityListUiState.Loading -> { /* Show loader */ }
                is CommunityListUiState.Success -> {
                    val communities = (uiState as CommunityListUiState.Success).communities

                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(communities, key = { it.id }) { community ->
                            CommunityCard(
                                community = community,
                                extendedColors = extendedColors, // PASSED DOWN
                                onClick = { onCommunityClick(community.id) }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(8.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun CommunityCard(
    community: Community,
    extendedColors: KithExtendedColors,
    onClick: () -> Unit
) {
    val memberCountToDisplay = community.memberCount ?: 0
    val memberCountFormatted = NumberFormat.getNumberInstance(Locale.US).format(memberCountToDisplay)

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = extendedColors.communityCardBg),
        border = BorderStroke(1.dp, extendedColors.communityCardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(extendedColors.communityImagePlaceholderBg),
                contentAlignment = Alignment.Center
            ) {
                if (!community.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = community.imageUrl,
                        contentDescription = "Community Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = KithIcons.Groups,
                        contentDescription = null,
                        tint = extendedColors.communityImagePlaceholderIcon
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = community.name,
                    color = extendedColors.communityTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 19.sp
                )
                Spacer(modifier = Modifier.height(4.dp))

                if (memberCountToDisplay > 0) {
                    Text(
                        text = "$memberCountFormatted Members",
                        color = extendedColors.communityTextSecondary,
                        fontSize = 13.sp
                    )
                } else {
                    Text(
                        text = "Active Community",
                        color = extendedColors.communityTextSecondary,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}