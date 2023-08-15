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