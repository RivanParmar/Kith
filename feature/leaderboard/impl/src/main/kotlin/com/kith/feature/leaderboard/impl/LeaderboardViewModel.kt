package com.kith.feature.leaderboard.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kith.core.data.repository.LeaderboardRepository
import com.kith.core.model.data.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val leaderboardRepository: LeaderboardRepository,
) : ViewModel() {

    private val _selectedSortBy = MutableStateFlow(LeaderboardTab.BY_XP)
    val selectedSortBy: StateFlow<LeaderboardTab> = _selectedSortBy.asStateFlow()

    val uiState: StateFlow<LeaderboardUiState> = _selectedSortBy
        .flatMapLatest { sortBy ->
            leaderboardRepository.getTopUsers(sortBy.categoryKey)
        }
        .map { LeaderboardUiState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LeaderboardUiState.Loading,
        )

    fun onTabChanged(sortBy: LeaderboardTab) {
        _selectedSortBy.value = sortBy
    }
}

sealed interface LeaderboardUiState {
    data object Loading : LeaderboardUiState

    data class Success(
        val users: List<UserProfile> = emptyList(),
    ) : LeaderboardUiState
}