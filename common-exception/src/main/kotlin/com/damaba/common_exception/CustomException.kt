package com.damaba.common_exception

abstract class CustomException(
    val httpStatusCode: Int,
    val exceptionType: CustomExceptionType,
    val optionalMessage: String? = null,
    override val cause: Throwable? = null,
) : RuntimeException() {
    val code: String
        get() = exceptionType.code

    override val message: String
        get() = if (optionalMessage.isNullOrBlank()) {
            exceptionType.message
        } else {
            "${exceptionType.message} $optionalMessage"
        }
}
