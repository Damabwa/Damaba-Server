package com.damaba.damaba.api.config

import com.damaba.common_logging.LogTrace
import com.damaba.common_logging.TraceStatus
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.stereotype.Component

@Component
@Aspect
class LogTraceAspect(private val logTrace: LogTrace) {
    @Around(
        value = "com.damaba.damaba.api.config.Pointcuts.controllerPoint() || " +
            "com.damaba.damaba.api.config.Pointcuts.useCasePoint() || " +
            "com.damaba.damaba.api.config.Pointcuts.domainServicePoint() || " +
            "com.damaba.damaba.api.config.Pointcuts.repositoryPoint()",
    )
    fun execute(joinPoint: ProceedingJoinPoint): Any? {
        var status: TraceStatus? = null
        try {
            val message = joinPoint.signature.toShortString()
            status = logTrace.begin(message)

            val result: Any? = joinPoint.proceed()

            logTrace.end(status)
            return result
        } catch (ex: Exception) {
            logTrace.exception((status)!!, ex)
            throw ex
        } finally {
            logTrace.releaseTraceId()
        }
    }
}

class Pointcuts {
    @Pointcut("execution(* com.damaba..controller..*Controller.*(..))")
    fun controllerPoint() {
    }

    @Pointcut("execution(* com.damaba..application..*UseCase.*(..))")
    fun useCasePoint() {
    }

    @Pointcut("execution(* com.damaba..domain..*Service.*(..))")
    fun domainServicePoint() {
    }

    @Pointcut("execution(* com.damaba..infra*..*Repository.*(..))")
    fun repositoryPoint() {
    }
}
