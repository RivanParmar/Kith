package com.kith.core.data.util

import com.kith.core.model.data.StreakState
import com.kith.core.model.data.Transaction
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

object StreakCalculator {

    fun calculate(transactions: List<Transaction>): StreakState {
        val today = LocalDate.now(ZoneId.systemDefault())

        // 1. Filter for Daily Reward transactions
        val dailyRewards = transactions.filter {
            it.title.contains("Daily Reward", ignoreCase = true)
        }

        if (dailyRewards.isEmpty()) {
            return StreakState(totalStreak = 0, isClaimedToday = false)
        }

        // 2. Parse timestamps to distinct LocalDates, sorted newest to oldest
        val rewardDates = dailyRewards.mapNotNull { tx ->
            try {
                Instant.parse(tx.timestamp)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
            } catch (e: Exception) {
                null
            }
        }.distinct().sortedDescending()

        if (rewardDates.isEmpty()) {
            return StreakState(totalStreak = 0, isClaimedToday = false)
        }

        val mostRecentReward = rewardDates.first()

        // 3. Determine if the streak is active
        val isClaimedToday = mostRecentReward == today
        val isClaimedYesterday = mostRecentReward == today.minusDays(1)

        // If not claimed today OR yesterday, the streak is broken
        if (!isClaimedToday && !isClaimedYesterday) {
            return StreakState(totalStreak = 0, isClaimedToday = false)
        }

        // 4. Count consecutive days backwards
        var streakCount = 0
        var expectedDate = mostRecentReward

        for (date in rewardDates) {
            if (date == expectedDate) {
                streakCount++
                expectedDate = expectedDate.minusDays(1)
            } else {
                break // Gap found, streak broken
            }
        }

        return StreakState(
            totalStreak = streakCount,
            isClaimedToday = isClaimedToday
        )
    }
}

