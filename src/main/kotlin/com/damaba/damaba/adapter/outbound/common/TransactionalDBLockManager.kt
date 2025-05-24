package com.damaba.damaba.adapter.outbound.common

import com.damaba.damaba.adapter.outbound.promotion.PromotionJpaRepository
import com.damaba.damaba.domain.common.constant.LockType
import com.damaba.damaba.domain.promotion.Promotion
import org.aspectj.lang.ProceedingJoinPoint
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import kotlin.reflect.KClass

@Component
class TransactionalDBLockManager(
    private val promotionJpaRepository: PromotionJpaRepository,
) : TransactionalLockManager {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun executeWithLock(
        joinPoint: ProceedingJoinPoint,
        lockType: LockType,
        domainType: KClass<*>,
        id: Any,
    ): Any {
        when (lockType) {
            LockType.PESSIMISTIC -> {
                when (domainType) {
                    Promotion::class -> promotionJpaRepository.acquirePessimisticLockById(id as Long)
                }
            }

            LockType.OPTIMISTIC -> throw UnsupportedOperationException("Optimistic locking is not currently supported.")
        }

        return joinPoint.proceed()
    }
}
