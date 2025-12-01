package com.apibackend.AppBackend.auth.dto.phone

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class PhoneOtpVerifyDto(
    @field:NotBlank(message = "Phone number is required")
    @field:Pattern(
        regexp = "^\\+?[0-9]{9,15}$",
        message = "Invalid phone number format"
    )
    val phoneNumber: String,

    @field:NotBlank(message = "OTP code is required")
    @field:Size(min = 6, max = 6, message = "OTP must be 6 digits")
    @field:Pattern(regexp = "^[0-9]{6}$", message = "OTP must contain only digits")
    val otpCode: String
)
