package com.apibackend.AppBackend.dto

import com.apibackend.AppBackend.model.UserRole
import java.time.LocalDateTime

data class UserDto(
        val id: Long,
        val email: String?,
        val phoneNumber: String?,
        val displayName: String,
        val avatarUrl: String?,
        val isActive: Boolean,
        val emailVerifiedAt: LocalDateTime?,
        val phoneNumberVerifiedAt: LocalDateTime?,
        val lastLoginAt: LocalDateTime?,
        val roles: Set<UserRole>,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime
)
