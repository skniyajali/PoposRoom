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

package com.niyaj.core.worker.initializers

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.niyaj.core.worker.workers.DELETE_DATA_WORKER_TAG
import com.niyaj.core.worker.workers.DataDeletionWorker
import com.niyaj.core.worker.workers.GENERATE_REPORT_WORKER_TAG
import com.niyaj.core.worker.workers.GenerateReportWorker
import com.niyaj.core.worker.workers.PRINT_ORDER_WORKER_TAG
import com.niyaj.core.worker.workers.PrintOrderWorker

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
    GenerateReportWorker.generateReportWorker(),
)

fun WorkManager.enqueueDeletionWorker() = this.enqueueUniquePeriodicWork(
    DELETE_DATA_WORKER_TAG,
    ExistingPeriodicWorkPolicy.KEEP,
    DataDeletionWorker.deletionWorker(),
)

fun WorkManager.enqueuePrintOrderWorker(orderId: Int) = this.enqueueUniqueWork(
    PRINT_ORDER_WORKER_TAG.plus("_$orderId"),
    ExistingWorkPolicy.APPEND_OR_REPLACE,
    PrintOrderWorker.printOrderWorker(orderId)
)