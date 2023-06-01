package com.niyaj.poposroom.features.common.utils

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val poposDispatcher: PoposDispatchers)

enum class PoposDispatchers {
    Default,
    IO,
}