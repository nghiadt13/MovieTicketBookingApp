package com.apibackend.AppBackend.auth.service

import com.apibackend.AppBackend.auth.dto.phone.*
import com.apibackend.AppBackend.auth.model.*
import com.apibackend.AppBackend.auth.repository.UserOtpRepository
import com.apibackend.AppBackend.auth.repository.UserRepository
import com.apibackend.AppBackend.auth.service.sms.SmsService
import com.apibackend.AppBackend.auth.service.sms.SmsServiceException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.SecureRandom
import java.time.Duration
import java.time.LocalDateTime

@Service
@Transactional
class OtpService(
    private val userRepository: UserRepository,
    private val userOtpRepository: UserOtpRepository,
    private val smsService: SmsService,
    @Value("\${otp.expiry-minutes:5}") private val otpExpiryMinutes: Long,
    @Value("\${otp.cooldown-seconds:60}") private val otpCooldownSeconds: Long,
    @Value("\${otp.max-attempts:5}") private val maxAttempts: Int,
    @Value("\${otp.rate-limit.per-minute:3}") private val rateLimitPerMinute: Int,
    @Value("\${otp.rate-limit.per-hour:10}") private val rateLimitPerHour: Int
) {
    private val logger = LoggerFactory.getLogger(OtpService::class.java)
    private val passwordEncoder = BCryptPasswordEncoder()
    private val secureRandom = SecureRandom()

    companion object {
        const val OTP_LENGTH = 6
    }

    /**
     * Send OTP to phone number
     */
    fun requestOtp(request: PhoneOtpRequestDto): OtpRequestResponseDto {
        val phoneNumber = normalizePhoneNumber(request.phoneNumber)

        // 1. Check user exists
        val user = userRepository.findByPhoneNumber(phoneNumber).orElse(null)
            ?: return OtpRequestResponseDto(
                success = false,
                message = "Phone number not registered",
                errorCode = "PHONE_NOT_FOUND"
            )

        // 2. Check user is active
        if (!user.isActive) {
            return OtpRequestResponseDto(
                success = false,
                message = "Account is inactive",
                errorCode = "ACCOUNT_INACTIVE"
            )
        }

        // 3. Check rate limit
        val rateLimitResult = checkRateLimit(phoneNumber)
        if (!rateLimitResult.allowed) {
            return OtpRequestResponseDto(
                success = false,
                message = "Too many requests. Please try again later",
                errorCode = "RATE_LIMITED",
                data = OtpRequestData(
                    phoneNumber = phoneNumber,
                    maskedPhone = maskPhoneNumber(phoneNumber),
                    expiresIn = 0,
                    retryAfter = rateLimitResult.retryAfter
                )
            )
        }

        // 4. Check cooldown
        val cooldownResult = checkCooldown(phoneNumber)
        if (!cooldownResult.allowed) {
            return OtpRequestResponseDto(
                success = false,
                message = "Please wait before requesting a new OTP",
                errorCode = "COOLDOWN_ACTIVE",
                data = OtpRequestData(
                    phoneNumber = phoneNumber,
                    maskedPhone = maskPhoneNumber(phoneNumber),
                    expiresIn = 0,
                    retryAfter = cooldownResult.retryAfter
                )
            )
        }

        // 5. Invalidate old OTPs
        userOtpRepository.invalidateAllOtps(
            user,
            OtpPurpose.PHONE_LOGIN,
            LocalDateTime.now()
        )

        // 6. Generate and save new OTP
        val otpCode = generateOtpCode()
        val otpHash = passwordEncoder.encode(otpCode)

        val userOtp = UserOtp(
            user = user,
            purpose = OtpPurpose.PHONE_LOGIN,
            channel = OtpChannel.SMS,
            contactValue = phoneNumber,
            codeHash = otpHash,
            expiresAt = LocalDateTime.now().plusMinutes(otpExpiryMinutes)
        )
        userOtpRepository.save(userOtp)

        // 7. Send SMS
        return try {
            smsService.sendOtp(phoneNumber, otpCode)

            logger.info("OTP sent to $phoneNumber for user ${user.id}")

            OtpRequestResponseDto(
                success = true,
                message = "OTP sent successfully",
                data = OtpRequestData(
                    phoneNumber = phoneNumber,
                    maskedPhone = maskPhoneNumber(phoneNumber),
                    expiresIn = (otpExpiryMinutes * 60).toInt(),
                    retryAfter = otpCooldownSeconds.toInt()
                )
            )
        } catch (e: SmsServiceException) {
            logger.error("Failed to send OTP SMS: ${e.message}", e)
            OtpRequestResponseDto(
                success = false,
                message = "SMS service temporarily unavailable",
                errorCode = "SMS_SERVICE_ERROR"
            )
        }
    }

    /**
     * Verify OTP
     */
    fun verifyOtp(request: PhoneOtpVerifyDto): OtpVerifyResult {
        val phoneNumber = normalizePhoneNumber(request.phoneNumber)

        // 1. Find user
        val user = userRepository.findByPhoneNumber(phoneNumber).orElse(null)
            ?: return OtpVerifyResult.UserNotFound

        // 2. Check user is active
        if (!user.isActive) {
            return OtpVerifyResult.AccountInactive
        }

        // 3. Find active OTP
        val otp = userOtpRepository.findActiveOtpByContact(
            phoneNumber,
            OtpPurpose.PHONE_LOGIN,
            OtpChannel.SMS,
            LocalDateTime.now()
        ).orElse(null)

        if (otp == null) {
            return OtpVerifyResult.OtpExpired
        }

        // 4. Check attempt count
        if (otp.attemptCount >= maxAttempts) {
            return OtpVerifyResult.MaxAttemptsExceeded
        }

        // 5. Verify OTP code
        if (!passwordEncoder.matches(request.otpCode, otp.codeHash)) {
            otp.attemptCount = (otp.attemptCount + 1).toShort()
            userOtpRepository.save(otp)

            val remaining = maxAttempts - otp.attemptCount
            return OtpVerifyResult.InvalidOtp(attemptsRemaining = remaining)
        }

        // 6. Mark OTP as consumed
        otp.consumedAt = LocalDateTime.now()
        userOtpRepository.save(otp)

        // 7. Update phone verified status
        if (user.phoneNumberVerifiedAt == null) {
            user.phoneNumberVerifiedAt = LocalDateTime.now()
        }
        user.lastLoginAt = LocalDateTime.now()
        userRepository.save(user)

        logger.info("OTP verified successfully for user ${user.id}")

        return OtpVerifyResult.Success(user)
    }

    /**
     * Resend OTP (alias for requestOtp with cooldown validation)
     */
    fun resendOtp(request: PhoneOtpRequestDto): OtpRequestResponseDto {
        return requestOtp(request)
    }

    // ========== Helper Methods ==========

    private fun generateOtpCode(): String {
        val sb = StringBuilder(OTP_LENGTH)
        repeat(OTP_LENGTH) {
            sb.append(secureRandom.nextInt(10))
        }
        return sb.toString()
    }

    private fun normalizePhoneNumber(phone: String): String {
        var normalized = phone.replace(Regex("[\\s\\-\\(\\)]"), "")

        if (normalized.startsWith("0")) {
            normalized = "+84" + normalized.substring(1)
        }

        if (!normalized.startsWith("+")) {
            normalized = "+$normalized"
        }

        return normalized
    }

    private fun maskPhoneNumber(phone: String): String {
        if (phone.length <= 4) return "****"
        return "*".repeat(phone.length - 4) + phone.takeLast(4)
    }

    private data class RateLimitResult(val allowed: Boolean, val retryAfter: Int = 0)

    private fun checkRateLimit(phoneNumber: String): RateLimitResult {
        val oneMinuteAgo = LocalDateTime.now().minusMinutes(1)
        val oneHourAgo = LocalDateTime.now().minusHours(1)

        val countLastMinute = userOtpRepository.countOtpsSentSince(
            phoneNumber, OtpPurpose.PHONE_LOGIN, oneMinuteAgo
        )

        if (countLastMinute >= rateLimitPerMinute) {
            return RateLimitResult(false, 60)
        }

        val countLastHour = userOtpRepository.countOtpsSentSince(
            phoneNumber, OtpPurpose.PHONE_LOGIN, oneHourAgo
        )

        if (countLastHour >= rateLimitPerHour) {
            return RateLimitResult(false, 3600)
        }

        return RateLimitResult(true)
    }

    private data class CooldownResult(val allowed: Boolean, val retryAfter: Int = 0)

    private fun checkCooldown(phoneNumber: String): CooldownResult {
        val lastOtp = userOtpRepository.findLatestOtp(phoneNumber, OtpPurpose.PHONE_LOGIN)
            .orElse(null)

        if (lastOtp != null) {
            val cooldownEnd = lastOtp.createdAt.plusSeconds(otpCooldownSeconds)
            val now = LocalDateTime.now()

            if (now.isBefore(cooldownEnd)) {
                val remaining = Duration.between(now, cooldownEnd).seconds.toInt()
                return CooldownResult(false, remaining)
            }
        }

        return CooldownResult(true)
    }
}

// Sealed class for verify result
sealed class OtpVerifyResult {
    data class Success(val user: User) : OtpVerifyResult()
    data class InvalidOtp(val attemptsRemaining: Int) : OtpVerifyResult()
    object OtpExpired : OtpVerifyResult()
    object MaxAttemptsExceeded : OtpVerifyResult()
    object UserNotFound : OtpVerifyResult()
    object AccountInactive : OtpVerifyResult()
}
