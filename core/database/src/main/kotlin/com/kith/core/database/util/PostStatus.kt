package com.kith.core.database.util

import androidx.room.TypeConverter

enum class PostStatus(val code: Int) {
    OPEN(0),
    PENDING_REVIEW(1),
    SOLVED(2)
}

internal class PostStatusConverter {
    @TypeConverter
    fun intToPostStatus(code: Int): PostStatus =
        PostStatus.entries.first { it.code == code }

    @TypeConverter
    fun postStatusToInt(status: PostStatus): Int = status.code
}