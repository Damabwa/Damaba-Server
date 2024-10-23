package com.damaba.damaba.config

import com.damaba.common_logging.Logger
import com.damaba.common_logging.MdcLogTraceManager
import jakarta.servlet.FilterChain
import jakarta.servlet.ReadListener
import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.Part
import org.springframework.http.MediaType
import org.springframework.util.StreamUtils
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingResponseWrapper
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.InputStreamReader
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.stream.Collectors

class LogApiInfoFilter : OncePerRequestFilter() {
    companion object {
        val LOG_BLACK_LIST = arrayOf(
            "/swagger",
            "/v3/api-docs",
            "/actuator",
        )
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        MdcLogTraceManager.setLogTraceIdIfAbsent()
        try {
            if (isAsyncDispatch(request) || LOG_BLACK_LIST.any { request.requestURI.startsWith(it) }) {
                filterChain.doFilter(request, response)
                return
            }

            val res = ResponseWrapper(response)
            val req = when {
                request.contentType == null -> RequestWrapper(request)
                request.contentType.contains(MediaType.MULTIPART_FORM_DATA_VALUE) -> request
                else -> RequestWrapper(request)
            }

            logRequest(req)
            filterChain.doFilter(req, res)
            logResponse(res)
            res.copyBodyToResponse()
        } finally {
            MdcLogTraceManager.clearAllLogTraceInfo()
        }
    }

    private fun logRequest(request: HttpServletRequest) {
        val uri = if (request.queryString != null) {
            val queryParams = URLDecoder.decode(request.queryString, StandardCharsets.UTF_8)
            "${request.requestURI}?$queryParams"
        } else {
            request.requestURI
        }

        if (request.contentType.isNullOrBlank()) {
            Logger.info("Request: ({}) uri={}", request.method, uri)
            return
        }

        if (request.contentType.contains(MediaType.MULTIPART_FORM_DATA_VALUE)) {
            try {
                val payload = request.parts.joinToString(", ") { part -> processPart(part) }
                Logger.info(
                    "Request: ({}) uri={}, content-type=multipart/form-data, content={}",
                    request.method,
                    request.requestURI,
                    payload,
                )
            } catch (ex: IllegalStateException) {
                Logger.info(
                    "Request: ({}) uri={}, content-type=multipart/form-data, request file size limit exceeded",
                    request.method,
                    request.requestURI,
                )
            }
            return
        }

        val contentByteArray = StreamUtils.copyToByteArray(request.inputStream)
        val payload = if (contentByteArray.isEmpty()) {
            "no content"
        } else {
            getPayload(request.contentType, contentByteArray)
        }
        Logger.info(
            "Request: ({}) uri={} content-type={} content={}",
            request.method,
            uri,
            request.contentType,
            payload,
        )
    }

    private fun logResponse(response: ContentCachingResponseWrapper) {
        val contentByteArray = StreamUtils.copyToByteArray(response.contentInputStream)
        val content = if (contentByteArray.isEmpty()) {
            "no content"
        } else {
            getPayload(response.contentType, contentByteArray)
        }
        Logger.info("Response: ({}) content-type={} content={}", response.status, response.contentType, content)
    }

    private fun getPayload(contentType: String, content: ByteArray): String {
        var payload = String(content)
        if (contentType.contains(MediaType.APPLICATION_JSON_VALUE)) {
            payload = payload.replace("\n\\s*".toRegex(), "")
        }
        return payload
    }

    private fun processPart(part: Part): String =
        if (part.contentType == null) {
            // 파일이 아닌, 텍스트 데이터인 경우
            val value = BufferedReader(InputStreamReader(part.inputStream))
                .lines()
                .collect(Collectors.joining(","))
            "${part.name}: $value"
        } else {
            // 파일 데이터인 경우
            val fileSize = String.format("%.2f", convertByteToKB(part.size))
            "${part.name}: ${part.submittedFileName}(${fileSize}KB)"
        }

    private fun convertByteToKB(sizeInByte: Long): Double = sizeInByte / 1024.0

    private class RequestWrapper(request: HttpServletRequest) : HttpServletRequestWrapper(request) {
        private val cachedInputStream: ByteArray = StreamUtils.copyToByteArray(request.inputStream)

        override fun getInputStream(): ServletInputStream = object : ServletInputStream() {
            private val inputStream = ByteArrayInputStream(cachedInputStream)
            override fun isFinished(): Boolean = inputStream.available() == 0
            override fun isReady(): Boolean = true
            override fun setReadListener(listener: ReadListener): Unit = throw UnsupportedOperationException()
            override fun read(): Int = inputStream.read()
        }
    }

    private class ResponseWrapper(response: HttpServletResponse) : ContentCachingResponseWrapper(response)
}
