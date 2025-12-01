package com.apibackend.AppBackend.auth.dto.phone

data class OtpRequestResponseDto(
    val success: Boolean,
    val message: String,
    val data: OtpRequestData? = null,
    val errorCode: String? = null
)

data class OtpRequestData(
    val phoneNumber: String,
    val maskedPhone: String,
    val expiresIn: Int,
    val retryAfter: Int
)

data class PhoneLoginResponseDto(
    val success: Boolean,
    val message: String,
    val data: PhoneLoginData? = null,
    val errorCode: String? = null
)

data class PhoneLoginData(
    val user: PhoneUserDto,
    val token: TokenDto
)

data class PhoneUserDto(
    val id: Long,
    val email: String?,
    val phoneNumber: String?,
    val displayName: String,
    val avatarUrl: String?,
    val isPhoneVerified: Boolean,
    val roles: List<String>
)

data class TokenDto(
    val accessToken: String,
    val refreshToken: String?,
    val tokenType: String = "Bearer",
    val expiresIn: Int
)
