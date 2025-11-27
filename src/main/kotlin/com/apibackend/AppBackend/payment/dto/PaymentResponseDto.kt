package com.apibackend.AppBackend.payment.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class PaymentResponseDto(
    val success: Boolean,

    @JsonProperty("transactionId")
    val transactionId: String? = null,

    @JsonProperty("qrCodeUrl")
    val qrCodeUrl: String? = null,

    val message: String
)

data class PaymentErrorDto(
    val success: Boolean = false,

    @JsonProperty("errorCode")
    val errorCode: String,

    val message: String
)
