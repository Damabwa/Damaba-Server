package com.damaba.common_exception

class ValidationException(message: String) :
    CustomException(
        httpStatusCode = 422,
        code = "VALIDATION_ERROR",
        message = message,
    )
