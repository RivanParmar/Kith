package com.kith.feature.browse.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kith.core.model.data.Community
import com.kith.core.model.data.Post
import com.kith.core.model.data.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Clock

/**
 * Navi Android BrowseViewModel
 *
 * Adheres strictly to:
 * - Unidirectional Data Flow with a single StateFlow<BrowseUiState>
 * - Single intent entry point onEvent(BrowseUiEvent)
 * - Thread-safe background computation using Dispatchers.Default
 */
@HiltViewModel
class BrowseViewModel @Inject constructor(
    // In production Navi architecture, inject repository or use case:
    // private val getBrowsePostsUseCase: GetBrowsePostsUseCase,
    // private val bookmarkPostUseCase: BookmarkPostUseCase,
    // private val analyticsTracker: AnalyticsTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        BrowseUiState(
            isLoading = true,
            filters = defaultFilterChips
        )
    )
    val uiState: StateFlow<BrowseUiState> = _uiState.asStateFlow()

    init {
        loadPosts()
    }

    /**
     * Single MVI entry point for all user intents
     */
    fun onEvent(event: BrowseUiEvent) {
        when (event) {
            is BrowseUiEvent.OnSearchQueryChanged -> handleSearchQueryChanged(event.query)
            BrowseUiEvent.OnSearchTriggered -> handleSearchTriggered()
            BrowseUiEvent.OnClearSearch -> handleClearSearch()
            is BrowseUiEvent.OnSearchBarExpandedChanged -> handleSearchBarExpandedChanged(event.isExpanded)
            is BrowseUiEvent.OnFilterChipToggled -> handleFilterChipToggled(event.filterId)
            is BrowseUiEvent.OnPostClicked -> handlePostClicked(event.post)
            BrowseUiEvent.OnDismissPostDetail -> handleDismissPostDetail()
            BrowseUiEvent.OnRefresh -> handleRefresh()
            BrowseUiEvent.OnDismissError -> _uiState.update { it.copy(errorMessage = null) }
        }
    }

    private fun loadPosts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // In production, fetch from repository:
                // val posts = getBrowsePostsUseCase()
                val posts = samplePostsList
                computeAndEmitState(
                    allPosts = posts,
                    filters = _uiState.value.filters,
                    searchQuery = _uiState.value.searchQuery,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Failed to load requests"
                    )
                }
            }
        }
    }

    private fun handleSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        recomputeFilteredPosts()
    }

    private fun handleSearchTriggered() {
        _uiState.update { it.copy(isSearchBarExpanded = false) }
        recomputeFilteredPosts()
    }

    private fun handleClearSearch() {
        _uiState.update { it.copy(searchQuery = "") }
        recomputeFilteredPosts()
    }

    private fun handleSearchBarExpandedChanged(isExpanded: Boolean) {
        _uiState.update { it.copy(isSearchBarExpanded = isExpanded) }
    }

    private fun handleFilterChipToggled(filterId: String) {
        val updatedFilters = _uiState.value.filters.map { chip ->
            if (filterId == "all") {
                chip.copy(isSelected = chip.id == "all")
            } else {
                when {
                    chip.id == "all" -> chip.copy(isSelected = false)
                    chip.id == filterId -> chip.copy(isSelected = !chip.isSelected)
                    else -> chip
                }
            }
        }
        // If no filter is selected, default back to "all"
        val hasAnySelected = updatedFilters.any { it.isSelected }
        val finalFilters = if (!hasAnySelected) {
            updatedFilters.map { it.copy(isSelected = it.id == "all") }
        } else {
            updatedFilters
        }
        _uiState.update { it.copy(filters = finalFilters) }
        recomputeFilteredPosts()
    }

    private fun handlePostClicked(post: Post) {
        _uiState.update { it.copy(selectedPost = post) }
    }

    private fun handleDismissPostDetail() {
        _uiState.update { it.copy(selectedPost = null) }
    }

    private fun handleRefresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            computeAndEmitState(
                allPosts = _uiState.value.allPosts,
                filters = _uiState.value.filters,
                searchQuery = _uiState.value.searchQuery,
                isLoading = false,
                isRefreshing = false
            )
        }
    }

    private fun recomputeFilteredPosts() {
        viewModelScope.launch {
            val currentState = _uiState.value
            computeAndEmitState(
                allPosts = currentState.allPosts,
                filters = currentState.filters,
                searchQuery = currentState.searchQuery,
                isLoading = false
            )
        }
    }

    /**
     * Executes post filtering on Dispatchers.Default to keep UI rendering smooth.
     */
    private suspend fun computeAndEmitState(
        allPosts: List<Post>,
        filters: List<BrowseFilterChip>,
        searchQuery: String,
        isLoading: Boolean = false,
        isRefreshing: Boolean = false
    ) = withContext(Dispatchers.Default) {
        val selectedFilterIds = filters.filter { it.isSelected }.map { it.id }.toSet()
        val isAllSelected = selectedFilterIds.contains("all")
        val filtered = allPosts.filter { post ->
            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                post.title.contains(searchQuery, ignoreCase = true) ||
                        post.content.contains(searchQuery, ignoreCase = true) ||
                        post.author.name.contains(searchQuery, ignoreCase = true) ||
                        post.community.name.contains(searchQuery, ignoreCase = true)
            }
            val matchesFilter = if (isAllSelected || selectedFilterIds.isEmpty()) {
                true
            } else {
                selectedFilterIds.any { filterId ->
                    when (filterId) {
                        "urgent" -> post.title.contains("urgent", ignoreCase = true) ||
                                post.content.contains("urgent", ignoreCase = true)
                        "academics" -> post.title.contains("study", ignoreCase = true) ||
                                post.content.contains("exam", ignoreCase = true) ||
                                post.community.name.contains("study", ignoreCase = true)
                        "rides" -> post.title.contains("ride", ignoreCase = true) ||
                                post.content.contains("car", ignoreCase = true) ||
                                post.content.contains("drive", ignoreCase = true)
                        "housing" -> post.title.contains("sublet", ignoreCase = true) ||
                                post.content.contains("room", ignoreCase = true) ||
                                post.content.contains("rent", ignoreCase = true)
                        else -> true
                    }
                }
            }
            matchesSearch && matchesFilter
        }
        _uiState.update { current ->
            current.copy(
                isLoading = isLoading,
                isRefreshing = isRefreshing,
                allPosts = allPosts,
                filters = filters,
                searchQuery = searchQuery,
                filteredPosts = filtered
            )
        }
    }
}

// Sample initial data — IDs must be unique, LazyColumn keys off Post.id
val samplePostsList = listOf(
    Post(
        "1", "Need a ride to campus", "Looking for a ride Monday morning", 5,
        User("1", "Preet Patel", null, false, 4.5f),
        Community("1", "Campus Rides", null), Clock.System.now(), true
    ),
    Post(
        "2", "Urgent: Exam study group", "Anyone free tonight to study?", 5,
        User("2", "Preet Patel", null, false, 4.5f),
        Community("2", "Academics", null), Clock.System.now(), true
    ),
    Post(
        "3", "Subletting my room", "Room available for next semester", 5,
        User("3", "Preet Patel", null, false, 4.5f),
        Community("3", "Housing", null), Clock.System.now(), true
    )
)