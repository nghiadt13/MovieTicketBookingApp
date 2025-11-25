package com.apibackend.AppBackend.common.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.NoHandlerFoundException

/** Global exception handler - xử lý tất cả exceptions trong application */
@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    /** Xử lý ApiException và các subclass */
    @ExceptionHandler(ApiException::class)
    fun handleApiException(ex: ApiException, request: WebRequest): ResponseEntity<ErrorResponse> {
        logger.error("ApiException: ${ex.message}", ex)

        val errorResponse =
                ErrorResponse(
                        status = ex.status.value(),
                        error = ex.status.reasonPhrase,
                        message = ex.message,
                        errorCode = ex.errorCode,
                        path = request.getDescription(false).removePrefix("uri=")
                )

        return ResponseEntity(errorResponse, ex.status)
    }

    /** Xử lý validation errors */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
            ex: MethodArgumentNotValidException,
            request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.error("Validation error: ${ex.message}")

        val fieldErrors =
                ex.bindingResult.fieldErrors.map { error ->
                    FieldError(
                            field = error.field,
                            message = error.defaultMessage ?: "Invalid value",
                            rejectedValue = error.rejectedValue
                    )
                }

        val errorResponse =
                ErrorResponse(
                        status = HttpStatus.BAD_REQUEST.value(),
                        error = HttpStatus.BAD_REQUEST.reasonPhrase,
                        message = "Validation failed",
                        errorCode = "VALIDATION_ERROR",
                        path = request.getDescription(false).removePrefix("uri="),
                        details = fieldErrors
                )

        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    /** Xử lý 404 - Not Found */
    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNoHandlerFoundException(
            ex: NoHandlerFoundException,
            request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.error("No handler found: ${ex.message}")

        val errorResponse =
                ErrorResponse(
                        status = HttpStatus.NOT_FOUND.value(),
                        error = HttpStatus.NOT_FOUND.reasonPhrase,
                        message = "Endpoint not found: ${ex.requestURL}",
                        errorCode = "ENDPOINT_NOT_FOUND",
                        path = request.getDescription(false).removePrefix("uri=")
                )

        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    /** Xử lý IllegalArgumentException */
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(
            ex: IllegalArgumentException,
            request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.error("IllegalArgumentException: ${ex.message}", ex)

        val errorResponse =
                ErrorResponse(
                        status = HttpStatus.BAD_REQUEST.value(),
                        error = HttpStatus.BAD_REQUEST.reasonPhrase,
                        message = ex.message ?: "Invalid argument",
                        errorCode = "INVALID_ARGUMENT",
                        path = request.getDescription(false).removePrefix("uri=")
                )

        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    /** Xử lý tất cả các exception khác */
    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(ex: Exception, request: WebRequest): ResponseEntity<ErrorResponse> {
        logger.error("Unexpected error: ${ex.message}", ex)

        val errorResponse =
                ErrorResponse(
                        status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        error = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
                        message = "An unexpected error occurred",
                        errorCode = "INTERNAL_ERROR",
                        path = request.getDescription(false).removePrefix("uri=")
                )

        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
