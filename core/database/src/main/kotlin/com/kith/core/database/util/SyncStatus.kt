package com.kith.core.database.util

import androidx.room.TypeConverter

enum class SyncStatus(val code: Int) {
    SYNCED(0),
    PENDING_CREATE(1),
    PENDING_UPDATE(2),
    PENDING_DELETE(3),
}

internal class SyncStatusConverter {
    @TypeConverter
    fun intToSyncStatus(code: Int): SyncStatus =
        SyncStatus.entries.first { it.code == code }

    @TypeConverter
    fun syncStatusToInt(status: SyncStatus): Int = status.code
}