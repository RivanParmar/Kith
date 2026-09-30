package com.kith.feature.browse.impl

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kith.core.data.model.RecentSearchQuery
import com.kith.core.designsystem.component.KithMediumTopAppBar
import com.kith.core.designsystem.component.KithSearchBar
import com.kith.core.designsystem.icon.KithIcons
import com.kith.core.designsystem.theme.KithTheme
import com.kith.core.designsystem.theme.OutfitFontFamily
import com.kith.core.model.data.Post
import com.kith.core.ui.PostPreviewParameterProvider
import com.kith.core.ui.PostsFeedUiState
import com.kith.core.ui.postsFeed
import com.kith.feature.browse.api.R
import kotlinx.coroutines.launch

/**
 * Filter options for the Browse screen feed
 */
enum class BrowseFilter(val label: String) {
    ALL("All"),
    IN_PERSON("In-person"),
    VIRTUAL("Virtual")
}

@Composable
fun BrowseScreen(
    modifier: Modifier = Modifier,
    viewModel: BrowseViewModel = hiltViewModel(),
) {
    val feedState by viewModel.feedState.collectAsStateWithLifecycle()
    val recentSearchQueriesUiState by viewModel.recentSearchQueriesUiState.collectAsStateWithLifecycle()

    BrowseScreen(
        feedState = feedState,
        recentSearchQueriesUiState = recentSearchQueriesUiState,
        modifier = modifier,
        onSearchTriggered = viewModel::onSearchTriggered,
        onClearRecentSearches = viewModel::clearRecentSearches,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BrowseScreen(
    feedState: PostsFeedUiState,
    recentSearchQueriesUiState: RecentSearchQueriesUiState = RecentSearchQueriesUiState.Loading,
    modifier: Modifier = Modifier,
    initialFilter: BrowseFilter = BrowseFilter.ALL,
    onFilterSelected: ((BrowseFilter) -> Unit)? = null,
    onSearchTriggered: (String) -> Unit = {},
    onClearRecentSearches: () -> Unit = {},
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val searchBarState = rememberSearchBarState()
    val textFieldState = rememberTextFieldState()
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    // True when the user taps into the search bar or activates search
    val isSearching = searchBarState.targetValue == SearchBarValue.Expanded

    // Holds the currently active filter (All, In-person, or Online)
    var currentFilter by remember { mutableStateOf(initialFilter) }

    val inputField =
        @Composable {
            SearchBarDefaults.InputField(
                textFieldState = textFieldState,
                searchBarState = searchBarState,
                onSearch = { query ->
                    val trimmed = query.trim()
                    if (trimmed.isNotEmpty()) {
                        onSearchTriggered(trimmed)
                    }
                    scope.launch { searchBarState.animateToCollapsed() }
                    focusManager.clearFocus()
                },
                placeholder = {
                    Text(
                        modifier = Modifier.clearAndSetSemantics {},
                        text = "Search topics, classes, or skills..."
                    )
                },
                leadingIcon = {
                    if (isSearching) {
                        IconButton(
                            onClick = {
                                scope.launch { searchBarState.animateToCollapsed() }
                                focusManager.clearFocus()
                            }
                        ) {
                            Icon(
                                imageVector = KithIcons.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    } else {
                        Icon(
                            imageVector = KithIcons.Search,
                            contentDescription = "Search"
                        )
                    }
                },
                trailingIcon = if (isSearching || textFieldState.text.isNotEmpty()) {
                    {
                        IconButton(
                            onClick = {
                                textFieldState.clearText()
                            }
                        ) {
                            Icon(
                                imageVector = KithIcons.Cancel,
                                contentDescription = "Clear search"
                            )
                        }
                    }
                } else null
            )
        }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            KithMediumTopAppBar(
                titleRes = R.string.feature_browse_api_title,
                navigationIcon = null,
                navigationIconContentDescription = null,
                actionIcon = null,
                actionIconContentDescription = null,
                scrollBehavior = scrollBehavior,
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.ExtraBold
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            KithSearchBar(
                searchBarState = searchBarState,
                inputField = inputField,
                modifier = Modifier.padding(top = 18.dp)
            ) {
                RecentSearchesContent(
                    recentSearchQueriesUiState = recentSearchQueriesUiState,
                    onRecentSearchClicked = { query ->
                        textFieldState.setTextAndPlaceCursorAtEnd(query)
                        onSearchTriggered(query)
                        scope.launch { searchBarState.animateToCollapsed() }
                        focusManager.clearFocus()
                    },
                    onClearRecentSearches = onClearRecentSearches,
                )
            }

            // Dynamic Row for "All", "In-person", "Online" Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                BrowseFilter.entries.forEach { filter ->
                    FilterChip(
                        selected = currentFilter == filter,
                        onClick = {
                            currentFilter = filter
                            onFilterSelected?.invoke(filter)
                        },
                        label = {
                            Text(filter.label)
                        },
                        shape = RoundedCornerShape(24.dp),
                    )
                }
            }

            // Grid displaying posts
            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                columns = GridCells.Adaptive(300.dp),
                contentPadding = PaddingValues(start = 10.dp, end = 10.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                postsFeed(feedState)
            }
        }
    }
}

@Composable
private fun RecentSearchesContent(
    recentSearchQueriesUiState: RecentSearchQueriesUiState,
    onRecentSearchClicked: (String) -> Unit,
    onClearRecentSearches: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (recentSearchQueriesUiState) {
        RecentSearchQueriesUiState.Loading -> Unit
        is RecentSearchQueriesUiState.Success -> {
            if (recentSearchQueriesUiState.recentQueries.isNotEmpty()) {
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Recent searches",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                        )
                        TextButton(
                            onClick = onClearRecentSearches,
                        ) {
                            Text(
                                text = "Clear all",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        items(
                            items = recentSearchQueriesUiState.recentQueries,
                            key = { it.query },
                        ) { recentSearch ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onRecentSearchClicked(recentSearch.query) }
                                    .padding(vertical = 12.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                Icon(
                                    imageVector = KithIcons.History,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Text(
                                    text = recentSearch.query,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun BrowseScreenPreview(
    @PreviewParameter(PostPreviewParameterProvider::class)
    posts: List<Post>
) {
    KithTheme {
        BrowseScreen(
            feedState = PostsFeedUiState.Success(posts),
            recentSearchQueriesUiState = RecentSearchQueriesUiState.Success(
                recentQueries = listOf(
                    RecentSearchQuery(query = "React"),
                    RecentSearchQuery(query = "Math 201"),
                    RecentSearchQuery(query = "Tutoring"),
                )
            ),
        )
    }
}