package com.kith.feature.browse.impl

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kith.core.model.data.Community
import com.kith.core.model.data.Post
import com.kith.core.model.data.User
import com.kith.core.ui.PostCard
import kotlin.time.Clock

@Preview
@Composable
private fun BrowsePreview() {
    BrowseRequestsScreen(
        posts = listOf(
            Post(
                "1", "Title", "Content", 5, User("1", "Preet patel", null, false, 4.5f),
                Community("1", "Abc", null), Clock.System.now(), true
            ),
            Post(
                "2", "Title", "Content", 5, User("1", "Preet patel", null, false, 4.5f),
                Community("1", "Abc", null), Clock.System.now(), true
            ),
            Post(
                "3", "Title", "Content", 5, User("1", "Preet patel", null, false, 4.5f),
                Community("1", "Abc", null), Clock.System.now(), true
            )
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseRequestsScreen(posts: List<Post>) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFFFFFFFF),
        topBar = {
            TopAppBar(
                title = { Text("Browse Request") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFFFFF)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {

            // Standard Filled TextField styled as a Search Pill
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .onFocusChanged { focusState ->
                        // Triggers the dynamic search area below when clicked
                        isSearchActive = focusState.isFocused
                    },
                placeholder = { Text("Search") },
                singleLine = true,
                shape = RoundedCornerShape(50), // Fully rounded corners
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF3F4F6),
                    unfocusedContainerColor = Color(0xFFF3F4F6),
                    // Hide the default bottom lines
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )

            // Dynamic area that opens when the user clicks the search bar
            AnimatedVisibility(visible = isSearchActive) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .heightIn(max = 200.dp)
                ) {
                    Text(
                        text = "Recent searches or suggestions go here...",
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            // Hide the main content when actively searching
            if (!isSearchActive) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    FilterChipItem()
                    FilterChipItem()
                    FilterChipItem()
                }

                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(posts, key = { post -> post.id }) { post ->
                        PostCard(post)
                    }
                }
            }
        }
    }
}

@Composable
fun FilterChipItem() {
    var selected by remember { mutableStateOf(false) }
    FilterChip(
        shape = RoundedCornerShape(24.dp),
        onClick = { selected = !selected },
        label = {
            Text("In-person")
        },
        selected = selected,
    )
}