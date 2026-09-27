package com.kith.core.model.data

data class StreakState(
    val totalStreak: Int = 0,
    val isClaimedToday: Boolean = false,
) {
    val currentUIRewardDay: Int
        get() {
            val targetStreak = if (isClaimedToday) totalStreak else totalStreak + 1
            return if (targetStreak == 0) 1 else ((targetStreak - 1) % 7) + 1
        }

    val isBonusDay: Boolean
        get() = currentUIRewardDay == 7
}