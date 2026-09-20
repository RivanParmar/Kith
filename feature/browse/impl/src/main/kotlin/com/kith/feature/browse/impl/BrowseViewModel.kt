package com.kith.feature.browse.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kith.core.data.repository.PostRepository
import com.kith.core.ui.PostsFeedUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BrowseViewModel @Inject constructor(
    postRepository: PostRepository,
) : ViewModel() {

    val feedState: StateFlow<PostsFeedUiState> = postRepository.getAllPostsStream()
        .map { PostsFeedUiState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PostsFeedUiState.Loading,
        )
}