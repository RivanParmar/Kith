package com.kith.core.data.repository

import com.kith.core.database.dao.UserDao
import com.kith.core.model.data.WalletData
import com.kith.core.network.KithAuthDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OfflineFirstWalletRepository @Inject constructor(
    private val userDao: UserDao,
    private val authDataSource: KithAuthDataSource
) : WalletRepository {

    override fun getWalletDataStream(): Flow<WalletData> {
        val userId = authDataSource.currentUserId()


        if (userId.isNullOrEmpty()) {
            return flowOf(
                WalletData(
                    balance = 0,
                    level = 1,
                    nextTierXp = 1000
                )
            )
        }

        return userDao.getUserWalletStream(userId).map { xp ->
            val safeXp = xp ?: 0

            val currentLevel = (safeXp / 1000) + 1
            val nextTierXp = currentLevel * 1000

            WalletData(
                balance = safeXp,
                level = currentLevel,
                nextTierXp = nextTierXp
            )
        }
    }
}