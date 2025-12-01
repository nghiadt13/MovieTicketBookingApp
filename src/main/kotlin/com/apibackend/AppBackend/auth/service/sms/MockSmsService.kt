package com.apibackend.AppBackend.auth.service.sms

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class MockSmsService(
    @Value("\${app.name:MovieBooking}") private val appName: String
) : SmsService {

    private val logger = LoggerFactory.getLogger(MockSmsService::class.java)

    // Store last OTPs for testing (DEV ONLY)
    private val sentOtps = mutableMapOf<String, String>()

    override fun sendSms(phoneNumber: String, message: String): Boolean {
        logger.info("===========================================")
        logger.info("[MOCK SMS] To: $phoneNumber")
        logger.info("[MOCK SMS] Message: $message")
        logger.info("===========================================")
        return true
    }

    override fun sendOtp(phoneNumber: String, otpCode: String): Boolean {
        sentOtps[phoneNumber] = otpCode
        val message = "[$appName] Ma xac thuc cua ban la: $otpCode. " +
                "Ma co hieu luc trong 5 phut. " +
                "Khong chia se ma nay voi bat ky ai."

        logger.info("===========================================")
        logger.info("[MOCK SMS] OTP for $phoneNumber: $otpCode")
        logger.info("[MOCK SMS] Full message: $message")
        logger.info("===========================================")
        return true
    }

    // For testing only
    fun getLastOtp(phoneNumber: String): String? = sentOtps[phoneNumber]
}
