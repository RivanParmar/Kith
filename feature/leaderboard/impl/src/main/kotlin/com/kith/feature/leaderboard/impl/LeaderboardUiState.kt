package com.kith.feature.leaderboard.impl

enum class LeaderboardTab(val title: String, val categoryKey: String) {
    BY_XP("By XP", "xp"),
    BY_TASKS("By Tasks", "problems_solved"),
    BY_RATING("By Rating","rating" )
}