package com.kith.core.database.util

import androidx.room.TypeConverter

enum class SyncStatus(val code: Int) {
    DRAFT(0),
    SYNCED(1),
    PENDING_CREATE(2),
    PENDING_UPDATE(3),
    PENDING_DELETE(4),
}

internal class SyncStatusConverter {
    @TypeConverter
    fun intToSyncStatus(code: Int): SyncStatus =
        SyncStatus.entries.first { it.code == code }

    @TypeConverter
    fun syncStatusToInt(status: SyncStatus): Int = status.code
}