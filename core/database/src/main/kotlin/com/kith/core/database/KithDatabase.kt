package com.kith.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kith.core.database.dao.CommunityDao
import com.kith.core.database.dao.PostDao
import com.kith.core.database.dao.RecentSearchQueryDao
import com.kith.core.database.dao.UserDao
import com.kith.core.database.model.CommunityEntity
import com.kith.core.database.model.PostEntity
import com.kith.core.database.model.RecentSearchQueryEntity
import com.kith.core.database.model.UserEntity
import com.kith.core.database.util.InstantConverter
import com.kith.core.database.util.PostStatusConverter
import com.kith.core.database.util.SyncStatusConverter

@Database(
    entities = [
        PostEntity::class,
        UserEntity::class,
        CommunityEntity::class,
        RecentSearchQueryEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(
    value = [
        InstantConverter::class,
        PostStatusConverter::class,
        SyncStatusConverter::class,
    ]
)
internal abstract class KithDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun userDao(): UserDao
    abstract fun communityDao(): CommunityDao
    abstract fun recentSearchQueryDao(): RecentSearchQueryDao
}