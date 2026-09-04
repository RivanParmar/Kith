package com.kith.core.database.di

import com.kith.core.database.KithDatabase
import com.kith.core.database.dao.CommunityDao
import com.kith.core.database.dao.PostDao
import com.kith.core.database.dao.RecentSearchQueryDao
import com.kith.core.database.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object DaosModule {
    @Provides
    fun providesPostDao(
        database: KithDatabase,
    ): PostDao = database.postDao()

    @Provides
    fun providesUserDao(
        database: KithDatabase,
    ): UserDao = database.userDao()

    @Provides
    fun providesCommunityDao(
        database: KithDatabase,
    ): CommunityDao = database.communityDao()

    @Provides
    fun providesRecentSearchQueryDao(
        database: KithDatabase,
    ): RecentSearchQueryDao = database.recentSearchQueryDao()
}