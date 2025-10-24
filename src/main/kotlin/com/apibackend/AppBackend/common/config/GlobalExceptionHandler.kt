package com.apibackend.AppBackend.common.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import java.time.OffsetDateTime
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

data class ApiError(
        val timestamp: OffsetDateTime = OffsetDateTime.now(),
        val status: Int,
        val error: String,
        val message: String? = null,
        val path: String? = null,
        val fieldErrors: Map<String, String>? = null
)

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(
            ex: MethodArgumentNotValidException,
            request: HttpServletRequest
    ): ResponseEntity<ApiError> {
        val errors =
                ex.bindingResult.allErrors.filterIsInstance<FieldError>().associate {
                    it.field to (it.defaultMessage ?: "invalid value")
                }

        val status = HttpStatus.BAD_REQUEST
        val body =
                ApiError(
                        status = status.value(),
                        error = status.reasonPhrase,
                        message = "Validation failed",
                        path = request.requestURI,
                        fieldErrors = errors
                )
        return ResponseEntity.status(status).body(body)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(
            ex: ConstraintViolationException,
            request: HttpServletRequest
    ): ResponseEntity<ApiError> {
        val status = HttpStatus.BAD_REQUEST
        val body =
                ApiError(
                        status = status.value(),
                        error = status.reasonPhrase,
                        message = ex.message,
                        path = request.requestURI
                )
        return ResponseEntity.status(status).body(body)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleNotReadable(
            ex: HttpMessageNotReadableException,
            request: HttpServletRequest
    ): ResponseEntity<ApiError> {
        val status = HttpStatus.BAD_REQUEST
        val body =
                ApiError(
                        status = status.value(),
                        error = status.reasonPhrase,
                        message = ex.mostSpecificCause.message,
                        path = request.requestURI
                )
        return ResponseEntity.status(status).body(body)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception, request: HttpServletRequest): ResponseEntity<ApiError> {
        val status = HttpStatus.INTERNAL_SERVER_ERROR
        val body =
                ApiError(
                        status = status.value(),
                        error = status.reasonPhrase,
                        message = ex.message,
                        path = request.requestURI
                )
        return ResponseEntity.status(status).body(body)
    }
}
