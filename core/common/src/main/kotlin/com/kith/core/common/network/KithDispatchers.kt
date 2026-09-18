package com.kith.core.common.network

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val kithDispatcher: KithDispatchers)

enum class KithDispatchers {
    Default,
    IO,
}