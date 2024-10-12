package com.damaba.user.infrastructure.kakao

import com.fasterxml.jackson.databind.ObjectMapper
import feign.Response
import feign.codec.ErrorDecoder

class KakaoKApiFeignErrorDecoder(private val mapper: ObjectMapper) : ErrorDecoder {
    private val errorDecoder: ErrorDecoder = ErrorDecoder.Default()

    override fun decode(methodKey: String?, response: Response?): Exception {
        if (response?.status() != null) {
            val errorResponse = mapper.readValue(response.body().asInputStream(), KakaoKApiErrorResponse::class.java)
            return KakaoApiException(errorResponse.code, errorResponse.msg)
        }
        return errorDecoder.decode(methodKey, response)
    }

    private data class KakaoKApiErrorResponse(
        val code: Int,
        val msg: String,
    )
}
