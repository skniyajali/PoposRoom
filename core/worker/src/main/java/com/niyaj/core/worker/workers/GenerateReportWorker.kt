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
import com.niyaj.common.utils.getStartTime
import com.niyaj.data.repository.ReportsRepository
import com.popos.core.notifications.Notifier
import com.popos.core.notifications.utils.REPORT_NOTIFICATION_ID
import com.popos.core.notifications.utils.reportWorkNotification
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

const val GENERATE_REPORT_INTERVAL_HOUR: Long = 1
const val GENERATE_REPORT_WORKER_TAG = "Report Generator Worker"

@HiltWorker
class GenerateReportWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workParams: WorkerParameters,
    private val reportsRepository: ReportsRepository,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
    private val notifier: Notifier,
) : CoroutineWorker(context, workParams) {

    override suspend fun getForegroundInfo(): ForegroundInfo =
        context.reportForegroundInfo()

    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        notifier.showReportGenerationNotification()

        val result = reportsRepository.generateReport(getStartTime)

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
        fun generateReportWorker() = PeriodicWorkRequestBuilder<DelegatingWorker>(
            GENERATE_REPORT_INTERVAL_HOUR,
            TimeUnit.HOURS,
        ).addTag(GENERATE_REPORT_WORKER_TAG)
            .setInputData(GenerateReportWorker::class.delegatedData())
            .build()
    }
}

/**
 * Foreground information for sync on lower API levels when sync workers are being
 * run with a foreground service
 */
fun Context.reportForegroundInfo() = ForegroundInfo(
    REPORT_NOTIFICATION_ID,
    reportWorkNotification(),
)
