package com.kith.feature.browse.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kith.core.data.repository.PostRepository
import com.kith.core.data.repository.RecentSearchRepository
import com.kith.core.network.KithAuthDataSource
import com.kith.core.ui.PostsFeedUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrowseViewModel @Inject constructor(
    postRepository: PostRepository,
    private val recentSearchRepository: RecentSearchRepository,
    authDataSource: KithAuthDataSource,
) : ViewModel() {

    val currentUserId = authDataSource.currentUserId()

    val feedState: StateFlow<PostsFeedUiState> = postRepository.getAllPostsStream()
        .map { PostsFeedUiState.Success(it) }
        .stateIn(
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

    fun onSearchTriggered(query: String) {
        val trimmed = query.trim()
        if (trimmed.isNotBlank()) {
            viewModelScope.launch {
                recentSearchRepository.insertOrReplaceRecentSearch(trimmed)
            }
        }
    }

    fun clearRecentSearches() {
        viewModelScope.launch {
            recentSearchRepository.clearRecentSearches()
        }
    }
}