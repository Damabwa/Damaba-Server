package com.damaba.damaba.logger

import org.slf4j.MDC
import java.util.UUID

object MdcLogTraceManager {

    private const val LOG_TRACE_ID_MDC_KEY: String = "LogTraceId"
    private const val REQUEST_USER_ID_MDC_KEY: String = "RequestUserId"

    val logTraceId: String?
        get() = MDC.get(LOG_TRACE_ID_MDC_KEY)

    val requestUserId: Long?
        get() = MDC.get(REQUEST_USER_ID_MDC_KEY)?.toLong()

    fun setLogTraceIdIfAbsent() {
        if (logTraceId.isNullOrBlank()) {
            setRandomLogTraceId()
        }
    }

    fun setRequestUserIdIfAbsent(requestUserId: Long) {
        if (this.requestUserId == null) {
            setRequestUserId(requestUserId)
        }
    }

    private fun setRandomLogTraceId() {
        MDC.put(LOG_TRACE_ID_MDC_KEY, UUID.randomUUID().toString().substring(0, 8))
    }

    private fun setRequestUserId(requestUserId: Long) {
        MDC.put(REQUEST_USER_ID_MDC_KEY, requestUserId.toString())
    }

    fun clearAllLogTraceInfo() {
        MDC.remove(LOG_TRACE_ID_MDC_KEY)
        MDC.remove(REQUEST_USER_ID_MDC_KEY)
    }
}
