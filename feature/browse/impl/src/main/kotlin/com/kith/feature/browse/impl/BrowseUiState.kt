package com.kith.feature.browse.impl

import androidx.compose.runtime.Immutable
import com.kith.core.model.data.Post

/**
 * Navi Android Unidirectional Data Flow (UDF) Architecture Contract
 *
 * 1. BrowseUiState: Single immutable source of truth for the BrowseRequests screen.
 * 2. BrowseUiEvent: All user and lifecycle interactions dispatched to BrowseViewModel.
 */

// =============================================================================
// FILTER CHIP MODEL
// =============================================================================
@Immutable
data class BrowseFilterChip(
    val id: String,
    val label: String,
    val isSelected: Boolean = false
)

// Default filter tags matching community request categories
val defaultFilterChips = listOf(
    BrowseFilterChip(id = "all", label = "All Requests", isSelected = true),
    BrowseFilterChip(id = "urgent", label = "Urgent Help"),
    BrowseFilterChip(id = "academics", label = "Academics & Study"),
    BrowseFilterChip(id = "rides", label = "Campus Rides"),
    BrowseFilterChip(id = "housing", label = "Housing & Items")
)

// =============================================================================
// IMMUTABLE UI STATE (Navi Android Single Source of Truth)
// =============================================================================
@Immutable
data class BrowseUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val searchQuery: String = "",
    val isSearchBarExpanded: Boolean = false,
    val filters: List<BrowseFilterChip> = defaultFilterChips,
    val allPosts: List<Post> = emptyList(),
    val filteredPosts: List<Post> = emptyList(),
    val selectedPost: Post? = null,
    val errorMessage: String? = null
) {
    val hasActiveFilters: Boolean
        get() = searchQuery.isNotBlank() || filters.any { it.isSelected && it.id != "all" }

    val isListEmpty: Boolean
        get() = !isLoading && filteredPosts.isEmpty()

    val totalCount: Int
        get() = filteredPosts.size
}

// =============================================================================
// UI EVENTS / USER INTENTS (UI -> ViewModel)
// =============================================================================
sealed interface BrowseUiEvent {
    data class OnSearchQueryChanged(val query: String) : BrowseUiEvent
    data object OnSearchTriggered : BrowseUiEvent
    data object OnClearSearch : BrowseUiEvent
    data class OnSearchBarExpandedChanged(val isExpanded: Boolean) : BrowseUiEvent
    data class OnFilterChipToggled(val filterId: String) : BrowseUiEvent
    data class OnPostClicked(val post: Post) : BrowseUiEvent
    data object OnDismissPostDetail : BrowseUiEvent
    data object OnRefresh : BrowseUiEvent
    data object OnDismissError : BrowseUiEvent
}