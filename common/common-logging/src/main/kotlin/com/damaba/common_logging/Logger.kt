package com.damaba.common_logging

import org.slf4j.LoggerFactory

/**
 * Log trace 정보와 함께 로그를 남길 때 사용하기 위한 logger class.
 *
 * 기록하는 log trace info는 다음과 같음.
 * - Log trace id:  각 API 요청에 대해 고유하게 할당된 식별자를 의미한다.
 * - Request user id: (요청 정보에 요청 유저의 정보가 있는 경우) 요청한 유저의 PK
 */
object Logger {

    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * 전달받은 내용을 log trace id와 함께 trace level로 로깅한다.
     * Log trace id란 각 API 요청에 대해 고유하게 할당된 식별자를 의미한다.
     *
     * @param message 로그에 출력할 내용
     */
    fun trace(message: String, vararg args: Any?) {
        if (logger.isTraceEnabled) {
            logger.trace(buildLogMessage(message), *args)
        }
    }

    /**
     * 전달받은 내용을 log trace id와 함께 debug level로 로깅한다.
     * Log trace id란 각 API 요청에 대해 고유하게 할당된 식별자를 의미한다.
     *
     * @param message 로그에 출력할 내용
     */
    fun debug(message: String, vararg args: Any?) {
        if (logger.isDebugEnabled) {
            logger.debug(buildLogMessage(message), *args)
        }
    }

    /**
     * 전달받은 내용을 log trace id와 함께 info level로 로깅한다.
     * Log trace id란 각 API 요청에 대해 고유하게 할당된 식별자를 의미한다.
     *
     * @param message 로그에 출력할 내용
     */
    fun info(message: String, vararg args: Any?) {
        if (logger.isInfoEnabled) {
            logger.info(buildLogMessage(message), *args)
        }
    }

    /**
     * 전달받은 내용을 log trace id와 함께 warn level로 로깅한다.
     * Log trace id란 각 API 요청에 대해 고유하게 할당된 식별자를 의미한다.
     *
     * @param message 로그에 출력할 내용
     */
    fun warn(message: String, vararg args: Any?) {
        if (logger.isWarnEnabled) {
            logger.warn(buildLogMessage(message), *args)
        }
    }

    /**
     * 전달받은 내용을 log trace id와 함께 error level로 로깅한다.
     * Log trace id란 각 API 요청에 대해 고유하게 할당된 식별자를 의미한다.
     *
     * @param message 로그에 출력할 내용
     */
    fun error(message: String, vararg args: Any?) {
        if (logger.isErrorEnabled) {
            logger.error(buildLogMessage(message), *args)
        }
    }

    fun error(message: String, ex: Throwable?, vararg args: Any?) {
        if (logger.isErrorEnabled) {
            logger.error(buildLogMessage(message), *args, ex)
        }
    }

    private fun buildLogMessage(message: String): String {
        val logTraceId = MdcLogTraceManager.logTraceId ?: "(no-trace-id)"
        val requesterId = MdcLogTraceManager.requestUserId
        return if (requesterId == null) {
            "[$logTraceId] $message"
        } else {
            "[$logTraceId] [UserID: $requesterId] $message"
        }
    }
}
