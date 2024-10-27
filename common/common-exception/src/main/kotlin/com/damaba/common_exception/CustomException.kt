package com.damaba.common_exception

abstract class CustomException(
    val httpStatusCode: Int,
    val code: String,
    override val message: String,
    override val cause: Throwable? = null,
) : RuntimeException()
