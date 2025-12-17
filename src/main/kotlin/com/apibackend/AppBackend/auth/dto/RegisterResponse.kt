package com.apibackend.AppBackend.auth.dto

/**
 * DTO cho response đăng ký
 * Nếu success = true, trả về user info và token (auto login sau đăng ký)
 */
data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val user: UserDto? = null,
    val token: String? = null
)
