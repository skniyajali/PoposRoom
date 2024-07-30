/*
 * Copyright 2024 Sk Niyaj Ali
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.niyaj.core.worker.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.Resource.Error
import com.niyaj.common.result.Resource.Success
import com.niyaj.data.repository.DataDeletionRepository
import com.popos.core.notifications.Notifier
import com.popos.core.notifications.utils.DELETION_NOTIFICATION_ID
import com.popos.core.notifications.utils.deletionWorkNotification
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit.HOURS

const val DELETE_DATA_WORKER_TAG = "DataDeletionWorker"
const val DELETE_DATA_INTERVAL_HOUR: Long = 15

@HiltWorker
class DataDeletionWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workParams: WorkerParameters,
    private val deletionRepository: DataDeletionRepository,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
    private val notifier: Notifier,
) : CoroutineWorker(context, workParams) {

    override suspend fun getForegroundInfo(): ForegroundInfo =
        context.deletionForegroundInfo()

    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        val result = deletionRepository.deleteData()

        when (result) {
            is Error -> Result.failure()
            is Success -> {
                // Send the notifications
                notifier.showDataDeletionNotification()
                Result.success()
            }
        }
    }

    companion object {
        /**
         * Expedited periodic time work to delete data on app startup
         */
        fun deletionWorker() = PeriodicWorkRequestBuilder<DelegatingWorker>(
            DELETE_DATA_INTERVAL_HOUR,
            HOURS,
        ).addTag(DELETE_DATA_WORKER_TAG)
            .setInputData(DataDeletionWorker::class.delegatedData())
            .build()
    }
}

/**
 * Foreground information for sync on lower API levels when sync workers are being
 * run with a foreground service
 */
fun Context.deletionForegroundInfo() = ForegroundInfo(
    DELETION_NOTIFICATION_ID,
    deletionWorkNotification(),
)
