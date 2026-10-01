package com.kith.feature.browse.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kith.core.data.repository.PostRepository
import com.kith.core.data.repository.RecentSearchRepository
import com.kith.core.network.KithAuthDataSource
import com.kith.core.ui.PostsFeedUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrowseViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val recentSearchRepository: RecentSearchRepository,
    authDataSource: KithAuthDataSource,
) : ViewModel() {

    val currentUserId = authDataSource.currentUserId()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val feedState: StateFlow<PostsFeedUiState> = combine(
        postRepository.getAllPostsStream(),
        _searchQuery
    ) { posts, query ->
        val filteredPosts = if (query.isBlank()) {
            // Feed Mode: Show only OPEN posts
            posts.filter { it.status == "OPEN" }
        } else {
            posts.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.content.contains(query, ignoreCase = true)
            }
        }
        PostsFeedUiState.Success(filteredPosts)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PostsFeedUiState.Loading,
    )

    val recentSearchQueriesUiState: StateFlow<RecentSearchQueriesUiState> =
        recentSearchRepository.getRecentSearchQueries(limit = 10)
            .map { RecentSearchQueriesUiState.Success(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = RecentSearchQueriesUiState.Loading,
            )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onSearchTriggered(query: String) {
        val trimmed = query.trim()
        val userId = currentUserId

        if (trimmed.isNotBlank() && userId != null) {
            viewModelScope.launch {
                recentSearchRepository.insertOrReplaceRecentSearch(trimmed)

                postRepository.searchAndSyncPosts(trimmed, userId)
            }
        }
    }

    fun clearRecentSearches() {
        viewModelScope.launch {
            recentSearchRepository.clearRecentSearches()
        }
    }
}