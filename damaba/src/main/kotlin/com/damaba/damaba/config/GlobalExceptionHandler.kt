package com.damaba.damaba.config

import com.damaba.common_exception.CustomException
import com.damaba.common_logging.Logger
import jakarta.validation.ConstraintViolation
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    companion object {
        private const val GENERAL_ERROR_MESSAGE = "알 수 없는 서버 에러가 발생했습니다."
        private const val VALIDATION_ERROR_MESSAGE = "요청 데이터가 잘못되었습니다. 요청 데이터의 값 또는 형식이 잘못되었거나, 필수값이 누락되지는 않았는지 확인해주세요."
    }

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(ex: CustomException): ResponseEntity<ErrorResponse> {
        Logger.error("Custom exception raised", ex)
        return ResponseEntity
            .status(ex.httpStatusCode)
            .body(ErrorResponse(ex.code, ex.message))
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        Logger.error("Validation exception raised", ex)
        val errorDetails = ex.bindingResult.fieldErrors.map { fieldError ->
            ValidationErrorDetailResponse(fieldError.field, fieldError.defaultMessage)
        }
        return ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(ValidationErrorResponse("VALIDATION", VALIDATION_ERROR_MESSAGE, errorDetails))
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(ex: ConstraintViolationException): ResponseEntity<ValidationErrorResponse> {
        Logger.error("Validation exception raised", ex)
        val errorDetails = ex.constraintViolations.map { violation ->
            ValidationErrorDetailResponse(getFieldNameFromConstraintViolation(violation), violation.message)
        }
        return ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(ValidationErrorResponse("VALIDATION", VALIDATION_ERROR_MESSAGE, errorDetails))
    }

    override fun handleExceptionInternal(
        ex: Exception,
        body: Any?,
        headers: HttpHeaders,
        statusCode: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any> {
        Logger.error("Spring MVC Basic exception raised", ex)
        return ResponseEntity
            .status(statusCode)
            .body(ErrorResponse("UNHANDLED", "${ex.message}"))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<ErrorResponse> {
        Logger.error("UnHandled exception raised", ex)
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse("UNHANDLED", GENERAL_ERROR_MESSAGE + ex.message))
    }

    private fun getFieldNameFromConstraintViolation(violation: ConstraintViolation<*>): String {
        val propertyPath = violation.propertyPath.toString()
        val dotIdx = propertyPath.lastIndexOf(".")
        return propertyPath.substring(dotIdx + 1)
    }

    data class ErrorResponse(
        val code: String,
        val message: String,
    )

    data class ValidationErrorResponse(
        val code: String,
        val message: String,
        val errors: List<ValidationErrorDetailResponse>,
    )

    data class ValidationErrorDetailResponse(
        val field: String,
        val message: String?,
    )
}
