package com.kith.core.database.util

import androidx.room.TypeConverter

enum class PostStatus(val code: Int) {
    OPEN(0),
    ASSIGNED(1),
    PENDING_REVIEW(2),
    SOLVED(3)
}

internal class PostStatusConverter {
    @TypeConverter
    fun intToPostStatus(code: Int): PostStatus =
        PostStatus.entries.first { it.code == code }

    @TypeConverter
    fun postStatusToInt(status: PostStatus): Int = status.code
}