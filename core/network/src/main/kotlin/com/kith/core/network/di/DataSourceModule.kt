package com.kith.core.network.di

import com.kith.core.network.KithAuthDataSource
import com.kith.core.network.KithNetworkDataSource
import com.kith.core.network.supabase.SupabaseAuthDataSource
import com.kith.core.network.supabase.SupabaseNetworkDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface DataSourceModule {

    @Binds
    fun bindsAuthDataSource(
        authDataSource: SupabaseAuthDataSource,
    ): KithAuthDataSource

    @Binds
    fun bindsNetworkDataSource(
        networkDataSource: SupabaseNetworkDataSource,
    ): KithNetworkDataSource
}