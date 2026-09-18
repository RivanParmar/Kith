package com.kith.feature.leaderboard.impl

import androidx.compose.runtime.Immutable
/**
 * Navi Android Unidirectional Data Flow (UDF) Architecture Contract
 *
 * 1. UiState: Single immutable source of truth for the screen.
 * 2. UiEvent / UiIntent: All user or lifecycle interactions sent to ViewModel.
 * 3. UiEffect: Transient side effects (Navigation, Toast, Snackbar) consumed once.
 */
// =============================================================================
// DOMAIN / UI ENUMS & MODELS
// =============================================================================
enum class LeaderboardTab(val title: String) {
    BY_XP("By XP"),
    BY_TASKS("By Tasks"),
    BY_RATING("By Rating")
}
enum class Timeframe(val label: String, val multiplier: Float) {
    WEEKLY("This Week", 1.0f),
    MONTHLY("This Month", 3.8f),
    ALL_TIME("All Time", 11.5f)
}
@Immutable
data class LeaderboardUser(
    val id: String,
    val name: String,
    val department: String,
    val avatarUrl: String,
    val xp: Int,
    val tasks: Int,
    val rating: Float,
    val streakDays: Int = 5,
    val isCurrentUser: Boolean = false,
    val bio: String = ""
)
// =============================================================================
// IMMUTABLE UI STATE
// =============================================================================
@Immutable
data class LeaderboardUiState(
    val isLoading: Boolean = false,
    val selectedTab: LeaderboardTab = LeaderboardTab.BY_XP,
    val selectedTimeframe: Timeframe = Timeframe.WEEKLY,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val selectedUserForDetail: LeaderboardUser? = null,
    val allUsers: List<LeaderboardUser> = emptyList(),
    // Pre-computed lists in ViewModel to avoid expensive sorting during Compose layout
    val sortedUsers: List<LeaderboardUser> = emptyList(),
    val filteredUsers: List<LeaderboardUser> = emptyList(),
    val top1: LeaderboardUser? = null,
    val top2: LeaderboardUser? = null,
    val top3: LeaderboardUser? = null,
    val restUsers: List<LeaderboardUser> = emptyList(),
    val errorMessage: String? = null
) {
    val isPodiumVisible: Boolean
        get() = top1 != null && top2 != null && top3 != null
    val isSearchEmpty: Boolean
        get() = searchQuery.isNotBlank() && restUsers.isEmpty()
}
// =============================================================================
// UI EVENTS / USER INTENTS (User Action -> ViewModel)
// =============================================================================
sealed interface LeaderboardUiEvent {
    data class OnTabSelected(val tab: LeaderboardTab) : LeaderboardUiEvent
    data class OnTimeframeSelected(val timeframe: Timeframe) : LeaderboardUiEvent
    data class OnSearchQueryChanged(val query: String) : LeaderboardUiEvent
    object OnToggleSearch : LeaderboardUiEvent
    object OnClearSearch : LeaderboardUiEvent
    data class OnUserClicked(val user: LeaderboardUser) : LeaderboardUiEvent
    object OnDismissUserDetail : LeaderboardUiEvent
    data class OnAddXp(val amount: Int) : LeaderboardUiEvent
    object OnResetData : LeaderboardUiEvent
    object OnRefresh : LeaderboardUiEvent
}
