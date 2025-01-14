package com.damaba.damaba.config

import com.damaba.damaba.adapter.outbound.common.TransactionalDBLockManager
import com.damaba.damaba.domain.common.TransactionalLock
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component

@Aspect
@Component
class TransactionalLockAspect(private val transactionalLockManager: TransactionalDBLockManager) {
    @Around("@annotation(transactionalLock)")
    fun applyTransactionalLock(joinPoint: ProceedingJoinPoint, transactionalLock: TransactionalLock): Any {
        val methodSignature = joinPoint.signature as MethodSignature
        val idFieldIdx = methodSignature.parameterNames.indexOf(transactionalLock.idFieldName)
        if (idFieldIdx == -1) {
            throw IllegalArgumentException("Parameter '${transactionalLock.idFieldName}' does not exist in method '${methodSignature.name}'")
        }
        return transactionalLockManager.executeWithLock(
            joinPoint = joinPoint,
            lockType = transactionalLock.lockType,
            domainType = transactionalLock.domainType,
            id = joinPoint.args[idFieldIdx],
        )
    }
}
