package com.kith.feature.profile.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kith.core.data.repository.PostRepository
import com.kith.core.data.util.SyncManager
import com.kith.core.model.data.Post
import com.kith.core.network.KithAuthDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostHistoryViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val authDataSource: KithAuthDataSource,
    private val syncManager: SyncManager,
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(PostHistoryTab.ACTIVE)
    val selectedTab: StateFlow<PostHistoryTab> = _selectedTab

    val isSyncing = syncManager.isSyncing
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    fun setTab(tab: PostHistoryTab) {
        _selectedTab.value = tab
    }

    // Capture the synchronous ID when the ViewModel is initialized
    private val currentUserId = authDataSource.currentUserId()

    // Dynamically uses the specific database stream based on authentication status
    val uiState: StateFlow<PostHistoryUiState> = if (currentUserId.isNullOrEmpty()) {
        MutableStateFlow(PostHistoryUiState.Error("User not authenticated"))
    } else {
        combine(
            postRepository.getPostsByUserIdStream(currentUserId),
            _selectedTab
        ) { userPosts, currentTab ->

            // Filters only the specific status tabs
            val filteredPosts = userPosts.filter { post ->
                when (currentTab) {
                    PostHistoryTab.ACTIVE -> post.status == "OPEN"
                    PostHistoryTab.IN_PROGRESS -> post.status == "ASSIGNED" || post.status == "PENDING_REVIEW"
                    PostHistoryTab.COMPLETED -> post.status == "SOLVED"
                    else -> false
                }
            }

            PostHistoryUiState.Success(posts = filteredPosts)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PostHistoryUiState.Loading
        )
    }

    fun sync() {
        syncManager.requestSync()

        viewModelScope.launch {
            currentUserId?.let { userId ->
                try {
                    postRepository.syncUserPosts(userId)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}

enum class PostHistoryTab(val title: String) {
    ACTIVE("Active"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed")
}

sealed interface PostHistoryUiState {
    data object Loading : PostHistoryUiState
    data class Success(val posts: List<Post>) : PostHistoryUiState
    data class Error(val message: String) : PostHistoryUiState
}