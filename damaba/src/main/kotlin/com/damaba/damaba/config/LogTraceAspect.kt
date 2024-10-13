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
        value = "com.damaba.damaba.config.Pointcuts.controllerPoint() || " +
            "com.damaba.damaba.config.Pointcuts.servicePoint() || " +
            "com.damaba.damaba.config.Pointcuts.repositoryPoint()",
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
    @Pointcut("@within(org.springframework.stereotype.Controller) || @within(org.springframework.web.bind.annotation.RestController)")
    fun controllerPoint() {
    }

    @Pointcut("@within(org.springframework.stereotype.Service)")
    fun servicePoint() {
    }

    @Pointcut("@within(org.springframework.stereotype.Repository)")
    fun repositoryPoint() {
    }
}
