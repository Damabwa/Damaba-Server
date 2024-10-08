package com.damaba.damaba.config

import com.damaba.common_logging.Logger
import com.damaba.common_logging.MdcLogTraceManager
import jakarta.servlet.FilterChain
import jakarta.servlet.ReadListener
import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.util.ObjectUtils
import org.springframework.util.StreamUtils
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingResponseWrapper
import java.io.ByteArrayInputStream
import java.io.IOException

class LogApiInfoFilter : OncePerRequestFilter() {
    companion object {
        val LOG_BLACK_LIST = arrayOf(
            "/swagger",
            "/v3/api-docs",
            "/actuator",
        )

        private val VISIBLE_TYPES: Set<MediaType> = setOf(
            MediaType.valueOf("text/*"),
            MediaType.APPLICATION_FORM_URLENCODED,
            MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_XML,
            MediaType.MULTIPART_FORM_DATA,
        )
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        MdcLogTraceManager.setLogTraceIdIfAbsent()
        runCatching {
            if (isAsyncDispatch(request)) {
                filterChain.doFilter(request, response)
            } else {
                val doLog = LOG_BLACK_LIST.none { request.requestURI.startsWith(it) }
                val responseWrapper = ResponseWrapper(response)
                runCatching {
                    // TODO: multipart/form-data type인 요청에 대해 세부 정보 로깅 기능 구현
                    if (isMultipartFormData(request.contentType)) {
                        Logger.info(
                            "Request: [{}] uri={}, payload=multipart/form-data",
                            request.method,
                            request.requestURI,
                        )
                        filterChain.doFilter(request, responseWrapper)
                    } else {
                        val requestWrapper = RequestWrapper(request)
                        if (doLog) logRequest(requestWrapper)
                        filterChain.doFilter(requestWrapper, responseWrapper)
                    }
                }.also {
                    if (doLog) logResponse(responseWrapper)
                    responseWrapper.copyBodyToResponse()
                }
            }
        }.also { MdcLogTraceManager.clearAllLogTraceInfo() }
    }

    @Throws(IOException::class)
    private fun logRequest(request: RequestWrapper) {
        var uri = request.requestURI
        val queryString = request.queryString
        if (queryString != null) {
            uri += "?$queryString"
        }

        val content = StreamUtils.copyToByteArray(request.inputStream)
        if (ObjectUtils.isEmpty(content)) {
            Logger.info("Request: [{}] uri={}", request.method, uri)
        } else {
            val payloadInfo = getPayloadInfo(request.contentType, content)
            Logger.info("Request: [{}] uri={}, {}", request.method, uri, payloadInfo)
        }
    }

    private fun logResponse(response: ContentCachingResponseWrapper) {
        Logger.info("Response: status={}", response.status)
    }

    private fun getPayloadInfo(contentType: String?, content: ByteArray): String {
        var type: String? = contentType
        var payloadInfo = "content-type=$type, payload="

        if (type == null) {
            type = MediaType.APPLICATION_JSON_VALUE
        }

        if (MediaType.valueOf(type) == MediaType.valueOf("text/html") ||
            MediaType.valueOf(
                type,
            ) == MediaType.valueOf("text/css")
        ) {
            return payloadInfo + "HTML/CSS Content"
        }
        if (!isMediaTypeVisible(MediaType.valueOf(type))) {
            return payloadInfo + "Binary Content"
        }

        if (content.size >= 10000) {
            return payloadInfo + "too many data."
        }

        val contentString = String(content)
        payloadInfo += if (type == MediaType.APPLICATION_JSON_VALUE) {
            contentString.replace("\n *".toRegex(), "").replace(",".toRegex(), ", ")
        } else {
            contentString
        }

        return payloadInfo
    }

    private fun isMultipartFormData(contentType: String?): Boolean =
        contentType != null && contentType.contains(MediaType.MULTIPART_FORM_DATA_VALUE)

    private fun isMediaTypeVisible(mediaType: MediaType): Boolean =
        VISIBLE_TYPES.any { visibleType -> visibleType.includes(mediaType) }

    class RequestWrapper(request: HttpServletRequest) : HttpServletRequestWrapper(request) {
        private val cachedInputStream: ByteArray? by lazy {
            StreamUtils.copyToByteArray(request.inputStream)
        }

        override fun getInputStream(): ServletInputStream {
            val byteArray = cachedInputStream ?: return super.getInputStream()
            return object : ServletInputStream() {
                private val inputStream = ByteArrayInputStream(byteArray)

                override fun isFinished(): Boolean = inputStream.available() == 0

                override fun isReady(): Boolean = true

                override fun setReadListener(listener: ReadListener): Unit = throw UnsupportedOperationException()

                override fun read(): Int = inputStream.read()
            }
        }
    }

    class ResponseWrapper(response: HttpServletResponse) : ContentCachingResponseWrapper(response)
}
