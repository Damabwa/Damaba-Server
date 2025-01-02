package com.damaba.damaba.logger

import org.springframework.stereotype.Component

@Component
class LogTrace {

    private val traceIdHolder: ThreadLocal<TraceId> = ThreadLocal<TraceId>()

    companion object {
        private const val START_PREFIX = "-->"
        private const val COMPLETE_PREFIX = "<--"
        private const val EX_PREFIX = "<X-"

        private fun addSpace(prefix: String, level: Int): String =
            (0 until level).joinToString("") { if (it == level - 1) "|$prefix" else "|   " }
    }

    fun begin(message: String): TraceStatus {
        syncTraceId()
        val traceId: TraceId = traceIdHolder.get()
        val startTimeMs = System.currentTimeMillis()
        Logger.info("${addSpace(START_PREFIX, traceId.level)}$message")
        return TraceStatus(traceId, startTimeMs, message)
    }

    private fun syncTraceId() {
        val traceId: TraceId? = traceIdHolder.get()
        if (traceId == null) {
            traceIdHolder.set(TraceId())
        } else {
            traceIdHolder.set(traceId.createNextId())
        }
    }

    fun end(status: TraceStatus) {
        complete(status, null)
    }

    fun exception(status: TraceStatus, e: Exception?) {
        complete(status, e)
    }

    private fun complete(status: TraceStatus, e: Exception?) {
        val stopTimeMs = System.currentTimeMillis()
        val resTimeMs: Long = stopTimeMs - status.startTimeMillis
        val traceId: TraceId = status.traceId

        val space = if (e == null) addSpace(COMPLETE_PREFIX, traceId.level) else addSpace(EX_PREFIX, traceId.level)
        if (e == null) {
            Logger.info("{}{} time={}ms", space, status.message, resTimeMs)
        } else {
            Logger.error("{}{} time={}ms ex={}", space, status.message, resTimeMs, e.toString())
        }
    }

    fun releaseTraceId() {
        val traceId: TraceId = traceIdHolder.get()
        if (traceId.isFirstLevel) {
            traceIdHolder.remove()
        } else {
            traceIdHolder.set(traceId.createPrevId())
        }
    }
}

class TraceId {
    val id: String?
    val level: Int

    constructor() {
        this.id = createId()
        this.level = 0
    }

    private constructor(id: String?, level: Int) {
        this.id = id
        this.level = level
    }

    private fun createId(): String? = MdcLogTraceManager.logTraceId

    fun createNextId(): TraceId = TraceId(id, level + 1)

    fun createPrevId(): TraceId = TraceId(id, level - 1)

    val isFirstLevel: Boolean
        get() = level == 0
}

data class TraceStatus(
    val traceId: TraceId,
    val startTimeMillis: Long,
    val message: String,
)
