package com.kith.core.database.di

import android.content.Context
import androidx.room.Room
import com.kith.core.database.KithDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    @Provides
    @Singleton
    fun providesKithDatabase(
        @ApplicationContext context: Context,
    ): KithDatabase = Room.databaseBuilder(
        context,
        KithDatabase::class.java,
        "kith-database",
    ).build()
}