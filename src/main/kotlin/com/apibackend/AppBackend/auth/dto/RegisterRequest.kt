package com.apibackend.AppBackend.auth.dto

import jakarta.validation.constraints.*

/**
 * DTO cho request đăng ký tài khoản mới
 * Có thể đăng ký bằng email hoặc phone (ít nhất 1 trong 2)
 */
data class RegisterRequest(
    @field:Email(message = "Invalid email format")
    val email: String? = null,

    @field:Pattern(
        regexp = "^\\+?[0-9]{9,15}$",
        message = "Invalid phone number format"
    )
    val phoneNumber: String? = null,

    @field:NotBlank(message = "Password is required")
    @field:Size(min = 6, max = 100, message = "Password must be 6-100 characters")
    val password: String,

    @field:NotBlank(message = "Display name is required")
    @field:Size(min = 2, max = 100, message = "Display name must be 2-100 characters")
    val displayName: String
)
