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
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.feature.printer.bluetoothPrinter.BluetoothPrinter
import com.popos.core.notifications.Notifier
import com.popos.core.notifications.utils.ORDER_PRINT_NOTIFICATION_ID
import com.popos.core.notifications.utils.printOrderNotification
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

const val PRINT_ORDER_WORKER_TAG = "PrintOrderWorkerTag"

@HiltWorker
class PrintOrderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workParams: WorkerParameters,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
    private val bluetoothPrinter: BluetoothPrinter,
    private val notifier: Notifier,
) : CoroutineWorker(context, workParams) {

    override suspend fun getForegroundInfo(): ForegroundInfo =
        context.printOrderForegroundInfo(inputData.getInt("orderId", 0))

    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        val orderId = inputData.getInt("orderId", 0)
        try {
            if (orderId == 0) {
                Result.failure()
            } else {
                bluetoothPrinter
                    .printOrderWorker(orderId)
                    .fold(
                        { Result.success() },
                        {
                            notifier.showPrintingErrorNotification(orderId)
                            Result.failure(workDataOf("error" to "Printer Not Connected"))
                        },
                    )
            }
        } catch (e: Exception) {
            notifier.showPrintingErrorNotification(orderId)
            Result.failure(workDataOf("error" to "Printer Not Connected"))
        }
    }

    companion object {
        /**
         * Expedited periodic time work to delete data on app startup
         */
        fun printOrderWorker(orderId: Int) = OneTimeWorkRequestBuilder<DelegatingWorker>()
            .addTag(PRINT_ORDER_WORKER_TAG)
            .setInputData(
                Data.Builder()
                    .putString(
                        "RouterWorkerDelegateClassName",
                        PrintOrderWorker::class.qualifiedName,
                    )
                    .putInt("orderId", orderId)
                    .build(),
            )
            .build()
    }
}

/**
 * Foreground information for sync on lower API levels when sync workers are being
 * run with a foreground service
 */
fun Context.printOrderForegroundInfo(orderId: Int) = ForegroundInfo(
    ORDER_PRINT_NOTIFICATION_ID,
    printOrderNotification(orderId),
)
