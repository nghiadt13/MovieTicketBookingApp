package com.apibackend.AppBackend.common.exception

import java.time.LocalDateTime

/** Response format chuẩn cho tất cả error responses */
data class ErrorResponse(
        val timestamp: LocalDateTime = LocalDateTime.now(),
        val status: Int,
        val error: String,
        val message: String,
        val errorCode: String? = null,
        val path: String? = null,
        val details: List<FieldError>? = null
)

/** Chi tiết lỗi cho từng field (validation errors) */
data class FieldError(val field: String, val message: String, val rejectedValue: Any? = null)
