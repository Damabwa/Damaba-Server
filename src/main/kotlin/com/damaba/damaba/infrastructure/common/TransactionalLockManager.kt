package com.damaba.damaba.infrastructure.common

import com.damaba.damaba.domain.common.constant.LockType
import org.aspectj.lang.ProceedingJoinPoint
import kotlin.reflect.KClass

interface TransactionalLockManager {
    fun executeWithLock(joinPoint: ProceedingJoinPoint, lockType: LockType, domainType: KClass<*>, id: Any): Any
}
