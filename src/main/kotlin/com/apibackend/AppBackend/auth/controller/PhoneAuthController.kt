package com.apibackend.AppBackend.auth.controller

import com.apibackend.AppBackend.auth.dto.phone.*
import com.apibackend.AppBackend.auth.service.OtpService
import com.apibackend.AppBackend.auth.service.OtpVerifyResult
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth/phone")
@Tag(name = "Phone OTP Authentication", description = "Phone number login with OTP verification")
class PhoneAuthController(
    private val otpService: OtpService
) {

    @PostMapping("/request-otp")
    @Operation(
        summary = "Request OTP",
        description = "Send OTP to the registered phone number"
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "OTP sent or error response"),
        ApiResponse(responseCode = "400", description = "Validation error"),
        ApiResponse(responseCode = "404", description = "Phone number not found"),
        ApiResponse(responseCode = "429", description = "Rate limited")
    ])
    fun requestOtp(
        @Valid @RequestBody request: PhoneOtpRequestDto
    ): ResponseEntity<OtpRequestResponseDto> {
        val response = otpService.requestOtp(request)

        val status = when (response.errorCode) {
            "PHONE_NOT_FOUND" -> HttpStatus.NOT_FOUND
            "ACCOUNT_INACTIVE", "PHONE_BLOCKED" -> HttpStatus.FORBIDDEN
            "RATE_LIMITED", "COOLDOWN_ACTIVE" -> HttpStatus.TOO_MANY_REQUESTS
            "SMS_SERVICE_ERROR" -> HttpStatus.SERVICE_UNAVAILABLE
            null -> HttpStatus.OK
            else -> HttpStatus.BAD_REQUEST
        }

        return ResponseEntity.status(status).body(response)
    }

    @PostMapping("/verify-otp")
    @Operation(
        summary = "Verify OTP and Login",
        description = "Verify OTP code and return authentication token"
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Login successful"),
        ApiResponse(responseCode = "401", description = "Invalid or expired OTP"),
        ApiResponse(responseCode = "423", description = "Max attempts exceeded")
    ])
    fun verifyOtp(
        @Valid @RequestBody request: PhoneOtpVerifyDto
    ): ResponseEntity<PhoneLoginResponseDto> {
        return when (val result = otpService.verifyOtp(request)) {
            is OtpVerifyResult.Success -> {
                val user = result.user

                // Generate JWT token
                val accessToken = generateAccessToken(user.id!!)
                val refreshToken = generateRefreshToken(user.id!!)

                val response = PhoneLoginResponseDto(
                    success = true,
                    message = "Login successful",
                    data = PhoneLoginData(
                        user = PhoneUserDto(
                            id = user.id!!,
                            email = user.email,
                            phoneNumber = user.phoneNumber,
                            displayName = user.displayName,
                            avatarUrl = user.avatarUrl,
                            isPhoneVerified = user.phoneNumberVerifiedAt != null,
                            roles = user.roles.map { it.name.name }
                        ),
                        token = TokenDto(
                            accessToken = accessToken,
                            refreshToken = refreshToken,
                            expiresIn = 3600
                        )
                    )
                )
                ResponseEntity.ok(response)
            }

            is OtpVerifyResult.InvalidOtp -> {
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    PhoneLoginResponseDto(
                        success = false,
                        message = "Invalid OTP. ${result.attemptsRemaining} attempts remaining",
                        errorCode = "INVALID_OTP"
                    )
                )
            }

            OtpVerifyResult.OtpExpired -> {
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    PhoneLoginResponseDto(
                        success = false,
                        message = "OTP has expired. Please request a new one",
                        errorCode = "OTP_EXPIRED"
                    )
                )
            }

            OtpVerifyResult.MaxAttemptsExceeded -> {
                ResponseEntity.status(HttpStatus.LOCKED).body(
                    PhoneLoginResponseDto(
                        success = false,
                        message = "Maximum attempts exceeded. Please request a new OTP",
                        errorCode = "MAX_ATTEMPTS_EXCEEDED"
                    )
                )
            }

            OtpVerifyResult.UserNotFound -> {
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    PhoneLoginResponseDto(
                        success = false,
                        message = "Phone number not registered",
                        errorCode = "PHONE_NOT_FOUND"
                    )
                )
            }

            OtpVerifyResult.AccountInactive -> {
                ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    PhoneLoginResponseDto(
                        success = false,
                        message = "Account is inactive",
                        errorCode = "ACCOUNT_INACTIVE"
                    )
                )
            }
        }
    }

    @PostMapping("/resend-otp")
    @Operation(
        summary = "Resend OTP",
        description = "Invalidate old OTP and send a new one"
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "New OTP sent"),
        ApiResponse(responseCode = "429", description = "Cooldown active")
    ])
    fun resendOtp(
        @Valid @RequestBody request: PhoneOtpRequestDto
    ): ResponseEntity<OtpRequestResponseDto> {
        return requestOtp(request)
    }

    // ========== JWT Helper Methods ==========
    // TODO: Implement proper JWT generation using existing JWT service
    private fun generateAccessToken(userId: Long): String {
        return "access-token-$userId-${System.currentTimeMillis()}"
    }

    private fun generateRefreshToken(userId: Long): String {
        return "refresh-token-$userId-${System.currentTimeMillis()}"
    }
}
