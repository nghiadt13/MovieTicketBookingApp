package com.apibackend.AppBackend.payment.model

enum class PaymentStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED,
    CANCELLED,
    REFUNDED
}
