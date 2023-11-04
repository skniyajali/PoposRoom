package com.niyaj.core.worker.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.core.worker.initializers.deletionForegroundInfo
import com.niyaj.data.repository.DataDeletionRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

const val DELETE_DATA_WORKER_TAG = "Data Deletion Worker"
const val DELETE_DATA_INTERVAL_HOUR: Long = 15


@HiltWorker
class DataDeletionWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workParams: WorkerParameters,
    private val deletionRepository: DataDeletionRepository,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : CoroutineWorker(context, workParams) {

    override suspend fun getForegroundInfo(): ForegroundInfo =
        context.deletionForegroundInfo()


    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        val result = deletionRepository.deleteData()

        result.message?.let {
            Result.retry()

            Result.failure()
        }

        Result.success()
    }


    companion object {
        /**
         * Expedited periodic time work to generate report on app startup
         */
        fun deletionWorker() = PeriodicWorkRequestBuilder<DelegatingWorker>(
            DELETE_DATA_INTERVAL_HOUR,
            TimeUnit.DAYS
        ).addTag(DELETE_DATA_WORKER_TAG)
            .setInputData(DataDeletionWorker::class.delegatedData())
            .build()
    }
}