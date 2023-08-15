package com.niyaj.poposroom

import android.app.Application
import com.niyaj.core.worker.initializers.WorkInitializers
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class PoposApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        WorkInitializers.initialize(context = this)
    }
}