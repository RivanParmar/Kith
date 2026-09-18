package com.kith.feature.leaderboard.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.kith.core.navigation.Navigator
import com.kith.feature.leaderboard.api.navigation.LeaderboardNavKey
import com.kith.feature.leaderboard.impl.LeaderboardScreen

fun EntryProviderScope<NavKey>.leaderboardEntry(navigator: Navigator) {
    entry<LeaderboardNavKey> {
        LeaderboardScreen()
    }
}