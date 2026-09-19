package com.kith.feature.leaderboard.impl
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
/**
 * Navi Android LeaderboardViewModel
 *
 * Adheres to:
 * - Unidirectional Data Flow (Single StateFlow<LeaderboardUiState>)
 * - Intent-based event processing (onEvent)
 * - Safe offloading of sorting/filtering to Dispatchers.Default
 * - Channel-based single-execution UiEffects
 */
@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    // In production Navi architecture, inject repository / use cases here:
    // private val getLeaderboardUseCase: GetLeaderboardUseCase,
    // private val analyticsTracker: AnalyticsTracker
) : ViewModel() {
    // 1. Private Mutable StateFlow & Public Immutable StateFlow
    private val _uiState = MutableStateFlow(
        LeaderboardUiState(
            isLoading = true,
            allUsers = initialLeaderboardUsers
        )
    )
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()
    // 2. One-shot event channel for UI Effects (Snackbars, Navigation, Analytics)

    init {
        loadLeaderboardData()
    }
    /**
     * Single entry point for all user interactions (Navi MVI Pattern)
     */
    fun onEvent(event: LeaderboardUiEvent) {
        when (event) {
            is LeaderboardUiEvent.OnTabSelected -> handleTabSelected(event.tab)
            is LeaderboardUiEvent.OnTimeframeSelected -> handleTimeframeSelected(event.timeframe)
            is LeaderboardUiEvent.OnSearchQueryChanged -> handleSearchQueryChanged(event.query)
            LeaderboardUiEvent.OnToggleSearch -> handleToggleSearch()
            LeaderboardUiEvent.OnClearSearch -> handleClearSearch()
            is LeaderboardUiEvent.OnUserClicked -> handleUserClicked(event.user)
            LeaderboardUiEvent.OnDismissUserDetail -> handleDismissUserDetail()
            is LeaderboardUiEvent.OnAddXp -> handleAddXp(event.amount)
            LeaderboardUiEvent.OnResetData -> handleResetData()
            LeaderboardUiEvent.OnRefresh -> loadLeaderboardData()
        }
    }
    private fun loadLeaderboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Recompute initial ranking on background dispatcher
            computeAndEmitState(
                users = _uiState.value.allUsers,
                tab = _uiState.value.selectedTab,
                timeframe = _uiState.value.selectedTimeframe,
                searchQuery = _uiState.value.searchQuery,
                isLoading = false
            )
        }
    }
    private fun handleTabSelected(tab: LeaderboardTab) {
        if (_uiState.value.selectedTab == tab) return
        _uiState.update { it.copy(selectedTab = tab) }
        recomputeLeaderboard()
    }
    private fun handleTimeframeSelected(timeframe: Timeframe) {
        if (_uiState.value.selectedTimeframe == timeframe) return
        _uiState.update { it.copy(selectedTimeframe = timeframe) }
        recomputeLeaderboard()
    }
    private fun handleSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        recomputeLeaderboard()
    }
    private fun handleToggleSearch() {
        val nextActive = !_uiState.value.isSearchActive
        _uiState.update {
            it.copy(
                isSearchActive = nextActive,
                searchQuery = if (!nextActive) "" else it.searchQuery
            )
        }
        if (!nextActive) {
            recomputeLeaderboard()
        }
    }
    private fun handleClearSearch() {
        _uiState.update { it.copy(searchQuery = "") }
        recomputeLeaderboard()
    }
    private fun handleUserClicked(user: LeaderboardUser) {
        _uiState.update { it.copy(selectedUserForDetail = user) }
        viewModelScope.launch {


        }
    }
    private fun handleDismissUserDetail() {
        _uiState.update { it.copy(selectedUserForDetail = null) }
    }
    private fun handleAddXp(amount: Int) {
        viewModelScope.launch {
            val updatedUsers = _uiState.value.allUsers.map { user ->
                if (user.isCurrentUser) {
                    user.copy(
                        xp = user.xp + amount,
                        tasks = user.tasks + (amount / 50),
                        streakDays = user.streakDays + 1
                    )
                } else user
            }
            computeAndEmitState(
                users = updatedUsers,
                tab = _uiState.value.selectedTab,
                timeframe = _uiState.value.selectedTimeframe,
                searchQuery = _uiState.value.searchQuery
            )

        }
    }
    private fun handleResetData() {
        viewModelScope.launch {
            computeAndEmitState(
                users = initialLeaderboardUsers,
                tab = LeaderboardTab.BY_XP,
                timeframe = Timeframe.WEEKLY,
                searchQuery = "",
                isSearchActive = false
            )

        }
    }
    /**
     * Triggers asynchronous re-computation off the Main UI thread
     */
    private fun recomputeLeaderboard() {
        viewModelScope.launch {
            val currentState = _uiState.value
            computeAndEmitState(
                users = currentState.allUsers,
                tab = currentState.selectedTab,
                timeframe = currentState.selectedTimeframe,
                searchQuery = currentState.searchQuery
            )
        }
    }
    /**
     * Executes sorting and search filtering on Dispatchers.Default
     * to ensure Compose UI thread stays silky smooth at 120 FPS.
     */
    private suspend fun computeAndEmitState(
        users: List<LeaderboardUser>,
        tab: LeaderboardTab,
        timeframe: Timeframe,
        searchQuery: String,
        isLoading: Boolean = false,
        isSearchActive: Boolean? = null
    ) = withContext(Dispatchers.Default) {
        // 1. Sort users based on selected metric and timeframe multiplier
        val sorted = users.sortedByDescending { user ->
            when (tab) {
                LeaderboardTab.BY_XP -> (user.xp * timeframe.multiplier)
                LeaderboardTab.BY_TASKS -> (user.tasks * timeframe.multiplier)
                LeaderboardTab.BY_RATING -> user.rating
            }
        }
        // 2. Filter based on search query
        val filtered = if (searchQuery.isBlank()) {
            sorted
        } else {
            sorted.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.department.contains(searchQuery, ignoreCase = true)
            }
        }
        // 3. Extract Podium (Top 3) from sorted master list
        val top1 = sorted.getOrNull(0)
        val top2 = sorted.getOrNull(1)
        val top3 = sorted.getOrNull(2)
        // 4. Remaining users for the scrollable list (Rank 4+)
        val rest = filtered.filter { user ->
            val rank = sorted.indexOfFirst { it.id == user.id } + 1
            rank > 3
        }
        _uiState.update { current ->
            current.copy(
                isLoading = isLoading,
                allUsers = users,
                selectedTab = tab,
                selectedTimeframe = timeframe,
                searchQuery = searchQuery,
                isSearchActive = isSearchActive ?: current.isSearchActive,
                sortedUsers = sorted,
                filteredUsers = filtered,
                top1 = top1,
                top2 = top2,
                top3 = top3,
                restUsers = rest
            )
        }
    }
}
