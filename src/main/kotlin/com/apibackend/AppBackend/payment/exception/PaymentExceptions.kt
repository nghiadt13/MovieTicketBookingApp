package com.apibackend.AppBackend.payment.exception

import com.apibackend.AppBackend.common.exception.ApiException
import com.apibackend.AppBackend.common.exception.BadRequestException
import com.apibackend.AppBackend.common.exception.ResourceNotFoundException
import org.springframework.http.HttpStatus

// Booking related exceptions
class BookingNotFoundException(bookingId: Long) : ResourceNotFoundException(
    message = "Booking not found with id: $bookingId",
    errorCode = "BOOKING_NOT_FOUND"
)

class BookingExpiredException(bookingId: Long) : BadRequestException(
    message = "Booking has expired: $bookingId",
    errorCode = "BOOKING_EXPIRED"
)

class BookingAlreadyPaidException(bookingId: Long) : BadRequestException(
    message = "Booking has already been paid: $bookingId",
    errorCode = "BOOKING_ALREADY_PAID"
)

// Payment method exceptions
class PaymentMethodNotFoundException(paymentMethodId: String) : ResourceNotFoundException(
    message = "Payment method not found: $paymentMethodId",
    errorCode = "PAYMENT_METHOD_NOT_FOUND"
)

class PaymentMethodInvalidException(paymentMethodId: String) : BadRequestException(
    message = "Payment method is not available: $paymentMethodId",
    errorCode = "PAYMENT_METHOD_INVALID"
)

// Combo exceptions
class ComboNotFoundException(comboId: Long) : ResourceNotFoundException(
    message = "Combo not found with id: $comboId",
    errorCode = "COMBO_NOT_FOUND"
)

class ComboNotAvailableException(comboId: Long) : BadRequestException(
    message = "Combo is not available: $comboId",
    errorCode = "COMBO_NOT_AVAILABLE"
)

// Discount code exceptions
class InvalidDiscountCodeException(code: String) : BadRequestException(
    message = "Invalid discount code: $code",
    errorCode = "INVALID_DISCOUNT_CODE"
)

class DiscountExpiredException(code: String) : BadRequestException(
    message = "Discount code has expired: $code",
    errorCode = "DISCOUNT_EXPIRED"
)

class DiscountUsageLimitException(code: String) : BadRequestException(
    message = "Discount code usage limit reached: $code",
    errorCode = "DISCOUNT_USAGE_LIMIT"
)

class MinOrderNotMetException(code: String, minAmount: Long) : BadRequestException(
    message = "Minimum order amount of $minAmount VND not met for discount code: $code",
    errorCode = "MIN_ORDER_NOT_MET"
)

// Payment exceptions
class PaymentFailedException(message: String) : ApiException(
    status = HttpStatus.INTERNAL_SERVER_ERROR,
    message = message,
    errorCode = "PAYMENT_FAILED"
)

class PaymentNotFoundException(transactionId: String) : ResourceNotFoundException(
    message = "Payment not found with transaction id: $transactionId",
    errorCode = "PAYMENT_NOT_FOUND"
)
