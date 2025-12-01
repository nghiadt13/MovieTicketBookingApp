package com.apibackend.AppBackend.common.exception

import org.springframework.http.HttpStatus

/**
 * Base exception class cho tất cả API exceptions
 */
open class ApiException(
    val status: HttpStatus,
    override val message: String,
    val errorCode: String? = null
) : RuntimeException(message)

/**
 * Exception khi không tìm thấy resource
 */
open class ResourceNotFoundException(
    message: String,
    errorCode: String? = "RESOURCE_NOT_FOUND"
) : ApiException(HttpStatus.NOT_FOUND, message, errorCode)

/**
 * Exception khi request không hợp lệ
 */
open class BadRequestException(
    message: String,
    errorCode: String? = "BAD_REQUEST"
) : ApiException(HttpStatus.BAD_REQUEST, message, errorCode)

/**
 * Exception khi không có quyền truy cập
 */
class UnauthorizedException(
    message: String,
    errorCode: String? = "UNAUTHORIZED"
) : ApiException(HttpStatus.UNAUTHORIZED, message, errorCode)

/**
 * Exception khi bị cấm truy cập
 */
class ForbiddenException(
    message: String,
    errorCode: String? = "FORBIDDEN"
) : ApiException(HttpStatus.FORBIDDEN, message, errorCode)

/**
 * Exception khi có conflict (ví dụ: duplicate data)
 */
class ConflictException(
    message: String,
    errorCode: String? = "CONFLICT"
) : ApiException(HttpStatus.CONFLICT, message, errorCode)

/**
 * Exception cho lỗi server nội bộ
 */
class InternalServerException(
    message: String,
    errorCode: String? = "INTERNAL_SERVER_ERROR"
) : ApiException(HttpStatus.INTERNAL_SERVER_ERROR, message, errorCode)
