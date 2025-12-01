package com.apibackend.AppBackend.auth.service.sms

interface SmsService {
    /**
     * Send SMS to phone number
     * @param phoneNumber Phone number in international format (+84...)
     * @param message Message content
     * @return true if sent successfully
     * @throws SmsServiceException if error occurs
     */
    fun sendSms(phoneNumber: String, message: String): Boolean

    /**
     * Send OTP SMS with template
     * @param phoneNumber Phone number
     * @param otpCode OTP code
     * @return true if sent successfully
     */
    fun sendOtp(phoneNumber: String, otpCode: String): Boolean
}

class SmsServiceException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)
