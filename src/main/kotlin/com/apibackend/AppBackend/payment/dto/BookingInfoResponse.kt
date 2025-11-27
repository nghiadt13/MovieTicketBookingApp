package com.apibackend.AppBackend.payment.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class BookingInfoResponse(
    @JsonProperty("bookingInfo")
    val bookingInfo: BookingInfoDto
)

data class CombosResponse(
    val combos: List<ComboDto>
)

data class PaymentMethodsResponse(
    @JsonProperty("paymentMethods")
    val paymentMethods: List<PaymentMethodDto>
)
