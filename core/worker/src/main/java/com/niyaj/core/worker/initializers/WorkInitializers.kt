package com.niyaj.core.worker.initializers

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.niyaj.core.worker.workers.DELETE_DATA_WORKER_TAG
import com.niyaj.core.worker.workers.DataDeletionWorker
import com.niyaj.core.worker.workers.GENERATE_REPORT_WORKER_TAG
import com.niyaj.core.worker.workers.GenerateReportWorker

object WorkInitializers {

    // This method is initializes sync, the process that keeps the app's data current.
    // It is called from the app module's Application.onCreate() and should be only done once.
    fun initialize(context: Context) {
        WorkManager.getInstance(context).apply {
            // Run sync on app startup and ensure only one sync worker runs at any time
            enqueueReportWorker()

            enqueueDeletionWorker()
        }
    }
}


fun WorkManager.enqueueReportWorker() = this.enqueueUniquePeriodicWork(
    GENERATE_REPORT_WORKER_TAG,
    ExistingPeriodicWorkPolicy.KEEP,
    GenerateReportWorker.generateReportWorker()
)

fun WorkManager.enqueueDeletionWorker() = this.enqueueUniquePeriodicWork(
    DELETE_DATA_WORKER_TAG,
    ExistingPeriodicWorkPolicy.KEEP,
    DataDeletionWorker.deletionWorker()
)