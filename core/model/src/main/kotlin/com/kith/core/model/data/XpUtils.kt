package com.kith.core.model.data

// Thresholds chosen to match your UI (1 450 XP → Level 3, next tier 2 000 XP)
private val XP_THRESHOLDS = intArrayOf(0, 500, 1_000, 2_000, 4_000, 8_000, 16_000, 32_000)

fun computeLevel(xp: Int): Int =
    (XP_THRESHOLDS.indexOfLast { xp >= it } + 1).coerceAtLeast(1)

fun computeNextTierXp(level: Int): Int =
    if (level < XP_THRESHOLDS.size) XP_THRESHOLDS[level] else XP_THRESHOLDS.last()

