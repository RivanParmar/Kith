package com.kith.feature.home.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kith.core.data.repository.PostRepository
import com.kith.core.data.repository.WalletRepository
import com.kith.core.data.util.SyncManager
import com.kith.core.network.KithAuthDataSource
import com.kith.core.ui.PostsFeedUiState
import com.kith.core.ui.WalletUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    postRepository: PostRepository,
    walletRepository: WalletRepository,
    private val syncManager: SyncManager,
    authDataSource: KithAuthDataSource,
) : ViewModel() {

    init {
        syncManager.requestSync()
    }

    val currentUserId = authDataSource.currentUserId()

    val walletState: StateFlow<WalletUiState> = walletRepository.getWalletDataStream()
        .map { WalletUiState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = WalletUiState.Loading,
        )

    val feedState: StateFlow<PostsFeedUiState> = postRepository.getAllPostsStream()
        .map { PostsFeedUiState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PostsFeedUiState.Loading,
        )

    val isSyncing = syncManager.isSyncing
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    fun sync() {
        syncManager.requestSync()
    }
}