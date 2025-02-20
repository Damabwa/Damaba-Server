package com.damaba.damaba.adapter.outbound.common

import com.damaba.damaba.domain.common.LockType
import org.aspectj.lang.ProceedingJoinPoint
import kotlin.reflect.KClass

interface TransactionalLockManager {
    fun executeWithLock(joinPoint: ProceedingJoinPoint, lockType: LockType, domainType: KClass<*>, id: Any): Any
}
