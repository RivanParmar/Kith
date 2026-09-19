package com.kith.core.network.di

import com.kith.core.network.KithNetworkDataSource
import com.kith.core.network.supabase.SupabaseNetworkDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface DataSourceModule {

    @Binds
    fun bindNetworkDataSource(impl: SupabaseNetworkDataSource): KithNetworkDataSource
}