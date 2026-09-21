package com.kith.core.data.di

import com.kith.core.data.repository.AuthRepository
import com.kith.core.data.repository.DefaultRecentSearchRepository
import com.kith.core.data.repository.NotificationRepository
import com.kith.core.data.repository.NotificationRepositoryImpl
import com.kith.core.data.repository.OfflineFirstPostRepository
import com.kith.core.data.repository.OfflineFirstUserDataRepository
import com.kith.core.data.repository.OfflineFirstUserRepository
import com.kith.core.data.repository.PostRepository
import com.kith.core.data.repository.OfflineFirstAuthRepository
import com.kith.core.data.repository.OfflineFirstWalletRepository
import com.kith.core.data.repository.RecentSearchRepository
import com.kith.core.data.repository.TransactionRepository
import com.kith.core.data.repository.TransactionRepositoryImpl
import com.kith.core.data.repository.UserDataRepository
import com.kith.core.data.repository.UserRepository
import com.kith.core.data.repository.WalletRepository
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
    internal abstract fun bindsPostRepository(
        postRepository: OfflineFirstPostRepository,
    ): PostRepository

    @Binds
    abstract fun bindUserRepository(
        impl: OfflineFirstUserRepository
    ): UserRepository


    @Binds
    internal abstract fun bindTransactionRepository(
        impl: TransactionRepositoryImpl
    ): TransactionRepository

    @Binds
    internal abstract fun bindsAuthRepository(
        authRepository: OfflineFirstAuthRepository,
    ): AuthRepository

    @Binds
    internal abstract fun bindsNotificationRepository(
        notificationRepository: NotificationRepositoryImpl,
    ): NotificationRepository

    @Binds
    internal abstract fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor,
    ): NetworkMonitor

    @Binds
    abstract fun bindWalletRepository(
        walletRepository: OfflineFirstWalletRepository,
    ): WalletRepository
}