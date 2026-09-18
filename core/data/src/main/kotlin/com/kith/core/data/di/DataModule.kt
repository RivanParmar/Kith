package com.kith.core.data.di

import com.kith.core.data.repository.AuthRepository
import com.kith.core.data.repository.DefaultRecentSearchRepository
import com.kith.core.data.repository.OfflineFirstAuthRepository
import com.kith.core.data.repository.OfflineFirstUserDataRepository
import com.kith.core.data.repository.RecentSearchRepository
import com.kith.core.data.repository.UserDataRepository
import com.kith.core.data.util.ConnectivityManagerNetworkMonitor
import com.kith.core.data.util.NetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    internal abstract fun bindsRecentSearchRepository(
        recentSearchRepository: DefaultRecentSearchRepository,
    ): RecentSearchRepository

    @Binds
    internal abstract fun bindsUserDataRepository(
        userDataRepository: OfflineFirstUserDataRepository,
    ): UserDataRepository

    @Binds
    internal abstract fun bindsAuthRepositor(
        authRepository: OfflineFirstAuthRepository,
    ): AuthRepository

    @Binds
    internal abstract fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor,
    ): NetworkMonitor
}