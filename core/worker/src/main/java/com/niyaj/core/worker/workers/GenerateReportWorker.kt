package com.niyaj.core.worker.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.utils.getEndTime
import com.niyaj.common.utils.getStartTime
import com.niyaj.core.worker.initializers.reportForegroundInfo
import com.niyaj.data.repository.ReportsRepository
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
) : CoroutineWorker(context, workParams) {

    override suspend fun getForegroundInfo(): ForegroundInfo =
        context.reportForegroundInfo()

    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        setForeground(context.reportForegroundInfo())

        val startDate = getStartTime
        val endDate = getEndTime

        val result = reportsRepository.generateReport(startDate, endDate)

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
            TimeUnit.HOURS
        ).addTag(GENERATE_REPORT_WORKER_TAG)
            .setInputData(GenerateReportWorker::class.delegatedData())
            .build()

    }
}