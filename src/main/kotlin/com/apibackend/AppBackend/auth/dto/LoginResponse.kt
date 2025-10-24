package com.apibackend.AppBackend.auth.dto

data class LoginResponse(
        val success: Boolean,
        val message: String,
        val user: UserDto? = null,
        val token: String? = null
)
