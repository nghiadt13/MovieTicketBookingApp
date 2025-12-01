package com.apibackend.AppBackend.auth.dto.phone

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class PhoneOtpRequestDto(
    @field:NotBlank(message = "Phone number is required")
    @field:Pattern(
        regexp = "^\\+?[0-9]{9,15}$",
        message = "Invalid phone number format"
    )
    val phoneNumber: String
)
