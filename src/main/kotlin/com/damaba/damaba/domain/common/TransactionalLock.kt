package com.damaba.damaba.domain.common

import com.damaba.damaba.domain.common.constant.LockType
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class TransactionalLock(
    val lockType: LockType,
    val domainType: KClass<*>,
    val idFieldName: String,
)
