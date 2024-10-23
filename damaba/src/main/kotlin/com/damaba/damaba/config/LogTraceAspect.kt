package com.damaba.damaba.config

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
        value = "com.damaba.damaba.config.Pointcuts.controllerPointcuts() || " +
            "com.damaba.damaba.config.Pointcuts.useCasePointcuts() || " +
            "com.damaba.damaba.config.Pointcuts.domainServicePointcuts() || " +
            "com.damaba.damaba.config.Pointcuts.repositoryPointcuts() || " +
            "com.damaba.damaba.config.Pointcuts.infrastructureServicePointcuts() || " +
            "com.damaba.damaba.config.Pointcuts.eventListenerPointcuts()",
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
    fun controllerPointcuts() {
    }

    @Pointcut("execution(* com.damaba..application..*UseCase.*(..))")
    fun useCasePointcuts() {
    }

    @Pointcut("execution(* com.damaba..domain..*Service.*(..))")
    fun domainServicePointcuts() {
    }

    @Pointcut("execution(* com.damaba..infrastructure..*Repository.*(..))")
    fun repositoryPointcuts() {
    }

    @Pointcut("execution(* com.damaba..infrastructure..*Service.*(..))")
    fun infrastructureServicePointcuts() {
    }

    @Pointcut("execution(* com.damaba..infrastructure..*EventListener.*(..))")
    fun eventListenerPointcuts() {
    }
}
