package com.niyaj.core.worker.di

import com.niyaj.core.worker.status.MonitorWorkManager
import com.niyaj.data.utils.WorkMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface WorkerModule {

    @Binds
    fun bindsWorkMonitor(
        monitorWorkManager: MonitorWorkManager
    ): WorkMonitor


}