package com.niyaj.data.utils

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.Flow

/**
 * Utility for reporting app connectivity status
 */
@Stable
interface NetworkMonitor {
    val isOnline: Flow<Boolean>
}
