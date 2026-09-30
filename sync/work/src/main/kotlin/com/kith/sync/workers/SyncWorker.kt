package com.kith.sync.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import com.kith.core.data.Synchronizer
import com.kith.core.data.repository.CommunityRepository
import com.kith.core.data.repository.PostRepository
import com.kith.core.data.repository.UserRepository
import com.kith.sync.initializers.SyncConstraints
import com.kith.sync.initializers.syncForegroundInfo
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

@HiltWorker
internal class SyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val userRepository: UserRepository,
    private val communityRepository: CommunityRepository,
    private val postRepository: PostRepository,
) : CoroutineWorker(appContext, workerParams), Synchronizer {

    override suspend fun getForegroundInfo(): ForegroundInfo =
        appContext.syncForegroundInfo()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val syncedSuccessfully = awaitAll(
            async { userRepository.sync() },
            async { communityRepository.sync() },
            async { postRepository.sync() }
        ).all { it }

        if (syncedSuccessfully) {
            Result.success()
        } else {
            Result.retry()
        }
    }

    companion object {
        fun startUpSyncWork() = OneTimeWorkRequestBuilder<DelegatingWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setConstraints(SyncConstraints)
            .setInputData(SyncWorker::class.delegatedData())
            .build()
    }
}