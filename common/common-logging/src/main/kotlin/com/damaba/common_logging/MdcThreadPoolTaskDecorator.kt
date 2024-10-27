package com.damaba.common_logging

import org.slf4j.MDC
import org.springframework.core.task.TaskDecorator

/**
 * Multi thread 환경에서도 MDC context 정보가 유지될 수 있도록 하는 thread pool task decorator
 * Multi thread 환경을 위한 thread pool configuration에 decorator로 등록하여 사용한다.
 */
class MdcThreadPoolTaskDecorator : TaskDecorator {
    override fun decorate(runnable: Runnable): Runnable {
        val contextMap = MDC.getCopyOfContextMap()
        return Runnable {
            runCatching {
                MDC.setContextMap(contextMap)
                runnable.run()
            }.also { MDC.clear() }
        }
    }
}
