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

package com.niyaj.core.worker.status

import android.content.Context
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.niyaj.core.worker.initializers.enqueueDeletionWorker
import com.niyaj.core.worker.initializers.enqueueReportWorker
import com.niyaj.core.worker.workers.DELETE_DATA_WORKER_TAG
import com.niyaj.core.worker.workers.GENERATE_REPORT_WORKER_TAG
import com.niyaj.data.utils.WorkMonitor
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MonitorWorkManager @Inject constructor(
    @ApplicationContext private val context: Context,
) : WorkMonitor {

    private val workManager = WorkManager.getInstance(context)

    override val isGeneratingReport: Flow<Boolean> = workManager
        .getWorkInfosForUniqueWorkFlow(GENERATE_REPORT_WORKER_TAG)
        .map(List<WorkInfo>::anyRunning)
        .conflate()

    override val isDeletingData: Flow<Boolean> = workManager
        .getWorkInfosForUniqueWorkFlow(DELETE_DATA_WORKER_TAG)
        .map(List<WorkInfo>::anyRunning)
        .conflate()

    override fun requestGenerateReport() {
        workManager.enqueueReportWorker()
    }

    override fun requestDeletingData() {
        workManager.enqueueDeletionWorker()
    }
}

private fun List<WorkInfo>.anyRunning() = any { it.state == WorkInfo.State.RUNNING }
