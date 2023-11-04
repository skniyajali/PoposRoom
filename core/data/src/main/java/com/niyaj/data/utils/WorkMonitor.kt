package com.niyaj.data.utils

import kotlinx.coroutines.flow.Flow

/**
 * Reports on if worker is in progress
 */
interface WorkMonitor {
    val isGeneratingReport: Flow<Boolean>

    val isDeletingData: Flow<Boolean>

    fun requestGenerateReport()

    fun requestDeletingData()
}
