package com.kith.feature.community.impl

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kith.core.designsystem.theme.OutfitFontFamily
import com.kith.core.model.data.Community
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CommunitiesScreen(
    onCommunityClick: (String) -> Unit = {},
    viewModel: CommunityListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(containerColor = Color.White) { innerPadding ->
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
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)
            )

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
    onClick: () -> Unit
) {
    // FIX: Safely handle if memberCount is null or 0.
    // If you haven't added memberCount to your database yet, just hardcode a placeholder or hide it.
    val memberCountToDisplay = community.memberCount ?: 0
    val memberCountFormatted = NumberFormat.getNumberInstance(Locale.US).format(memberCountToDisplay)

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFECEDF3)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(56.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFFE3E6F0)),
                contentAlignment = Alignment.Center
            ) {
                // If you have images later, put an AsyncImage here!
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = community.name, color = Color(0xFF1B1F3B), fontSize = 15.sp, fontWeight = FontWeight.SemiBold, lineHeight = 19.sp)
                Spacer(modifier = Modifier.height(4.dp))

                // Only show members if greater than 0, otherwise show a generic subtitle
                if (memberCountToDisplay > 0) {
                    Text(text = "$memberCountFormatted Members", color = Color(0xFFA0A3B1), fontSize = 13.sp)
                } else {
                    Text(text = "Active Community", color = Color(0xFFA0A3B1), fontSize = 13.sp)
                }
            }
        }
    }
}