package com.apibackend.AppBackend.payment.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull

data class CalculatePaymentRequest(
    @field:NotNull(message = "Booking ID is required")
    @JsonProperty("bookingId")
    val bookingId: Long,

    @field:Valid
    val combos: List<ComboSelectionDto> = emptyList(),

    @JsonProperty("discountCode")
    val discountCode: String? = null
)
